package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.networking.payloads.LaunchContraptionPayload;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.nadia.utm.utm;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.nadia.utm.registry.attachment.utmAttachments.ENTERED_2313AG;

@ForceLoad
public class SpaceStateHandler {
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            MinecraftServer server = sPlayer.getServer();
            if (server == null) return;

            boolean inAG = sPlayer.serverLevel().dimension().equals(utmDimensions.AG_KEY);
            boolean enteredAG = sPlayer.getData(ENTERED_2313AG);
            Persistance.checkPersistance(sPlayer, server.getLevel(utmDimensions.AG_KEY), enteredAG, inAG);
            Breathability.checkSuffocating(sPlayer, inAG);
        }
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living && !entity.level().isClientSide) {
            if (entity.level().dimension().equals(utmDimensions.AG_KEY)) {
                var gravity = living.getAttribute(Attributes.GRAVITY);
                if (gravity != null && gravity.getBaseValue() != 0.12) {
                    gravity.setBaseValue(0.12);
                }
            }
        }
    }

    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            int jumpPower = boots.getEnchantmentLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(utmEnchantments.POWER_JUMP));
            if (jumpPower > 0) {
                Vec3 delta = player.getDeltaMovement();
                player.setDeltaMovement(delta.x, delta.y + (0.10D * jumpPower), delta.z);
            }
        }
    }

    public static final Set<Block> UNMODIFIED_BLOCKS = Set.of(
            Blocks.STONE,
            Blocks.COBBLESTONE,
            Blocks.ANDESITE,
            Blocks.STONE_BRICKS,
            Blocks.TUFF,
            Blocks.DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE
    );

    public static void launchRecieved(LaunchContraptionPayload payload, IPayloadContext context) {
        Player player = context.player();
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel ag = server.getLevel(utmDimensions.AG_KEY);
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (ag == null || overworld == null) return;

        AbstractContraptionEntity contraption;
        Entity vehicle = player.level().getEntity(payload.id());
        if (vehicle instanceof AbstractContraptionEntity c)
            contraption = c;
        else {
            contraption = null;
        }

        if (contraption == null) return;

        ServerLevel target = vehicle.level() == overworld ? ag : overworld;
        ServerLevel origin = vehicle.level() == overworld ? overworld : ag;

        if (vehicle.level() == target) return;

        Map<UUID, Integer> passengers = new HashMap<>(contraption.getContraption().getSeatMapping());
        for (UUID uuid : passengers.keySet()) {
            Entity e = origin.getEntity(uuid);
            if (e == null) continue;

            if (e.level() == ag) {
                passengers.remove(uuid);
                e.unRide();
            }
        }

        AABB bounds = contraption.getBoundingBox();
        double yOffset = (target.getLogicalHeight() + 512 + bounds.getYsize()) - contraption.getY();

        Entity cVehicle = contraption.getVehicle();
        if (cVehicle == null) return;

        AtomicReference<Entity> finalVehicle = new AtomicReference<>(null);
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), target.registryAccess(), ConnectionType.OTHER);
        contraption.writeSpawnData(buf);

        contraption.getContraption().getBlocks().clear();
        contraption.getContraption().disassembled = true;
        contraption.ejectPassengers();
        contraption.stopRiding();

        cVehicle.changeDimension(new DimensionTransition(
                target,
                cVehicle.position().add(0, yOffset, 0),
                cVehicle.getDeltaMovement(),
                cVehicle.getYRot(),
                cVehicle.getXRot(),
                finalVehicle::set
        ));

        int startTick = server.getTickCount();
        Runnable pollTask = new Runnable() {
            @Override
            public void run() {
                Entity newVehicle = finalVehicle.get();
                if (newVehicle != null || (server.getTickCount() - startTick) >= 200) {
                    warpContraption(contraption, target, yOffset, buf, newVehicle, passengers, origin);
                } else {
                    server.tell(new TickTask(server.getTickCount() + 1, this));
                }
            }
        };

        server.tell(new TickTask(server.getTickCount() + 1, pollTask));
    }

    private static void warpContraption(AbstractContraptionEntity prev, ServerLevel target, double yOffset, RegistryFriendlyByteBuf buf, @Nullable Entity newVehicle, Map<UUID, Integer> passengers, ServerLevel origin) {
        Vec3 targetPos = prev.position().add(0, yOffset, 0);
        float yRot = prev.getYRot();
        float xRot = prev.getXRot();

        AbstractContraptionEntity entity = (AbstractContraptionEntity) prev.getType().create(target);
        prev.discard();
        if (entity == null || newVehicle == null) return;

        entity.setPos(targetPos);
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        entity.readSpawnData(buf);

        if (entity.getContraption() == null) {
            utm.LOGGER.warn("[UTM] post-warp contraption null? ???");
            return;
        }

        target.addFreshEntity(entity);
        entity.startRiding(newVehicle, true);

        passengers.forEach((uuid, seatIndex) -> {
            Entity pass = origin.getEntity(uuid);

            if (pass != null)
                pass.changeDimension(new DimensionTransition(
                        target,
                        pass.position().add(0, yOffset, 0),
                        pass.getDeltaMovement(),
                        pass.getYRot(),
                        pass.getXRot(),
                        (newPass) -> {
                            entity.addSittingPassenger(newPass, seatIndex);
                            newPass.addTag("utm_reentry_landing");
                        }
                ));
        });
    }

    static {
        utmEvents.register(PlayerTickEvent.Post.class, SpaceStateHandler::onPlayerTick);
        utmEvents.register(EntityTickEvent.Post.class, SpaceStateHandler::onEntityTick);
        utmEvents.register(LivingEvent.LivingJumpEvent.class, SpaceStateHandler::onJump);
        utmEvents.register(LivingFallEvent.class, event -> {
            Entity entity = event.getEntity();

            if (entity.getTags().contains("utm_reentry_landing")) {
                event.setDamageMultiplier(0);
                event.setDistance(0);
                event.setCanceled(true);
                entity.removeTag("utm_reentry_landing");
            }
        });
    }
}
