package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.networking.payloads.LaunchContraptionPayload;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    static {
        utmEvents.register(PlayerTickEvent.Post.class, SpaceStateHandler::onPlayerTick);
        utmEvents.register(EntityTickEvent.Post.class, SpaceStateHandler::onEntityTick);
        utmEvents.register(LivingEvent.LivingJumpEvent.class, SpaceStateHandler::onJump);
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

        AbstractContraptionEntity contraption = null;
        Entity vehicle = player.level().getEntity(payload.id());
        if (vehicle instanceof AbstractContraptionEntity c)
            contraption = c;

        if (contraption == null) return;

        ServerLevel target = vehicle.level() == overworld ? ag : overworld;
        if (vehicle.level() == target) return;

        if (player.level() == ag && player.getVehicle() == vehicle)
            player.unRide();

        List<Entity> passengers = new ArrayList<>(List.copyOf(contraption.getPassengers()));

        if (player.level() == ag)
            passengers.remove(player);

        AABB bounds = contraption.getBoundingBox();
        double yOffset = (319 + bounds.getYsize()) - contraption.getY();

        CompoundTag nbt = contraption.getContraption().writeNBT(contraption.registryAccess(), false);

        Entity cVehicle = contraption.getVehicle();
        AtomicReference<Entity> finalVehicle = new AtomicReference<>(null);
        contraption.getContraption().getBlocks().clear();
        contraption.changeDimension(new DimensionTransition(
                target,
                contraption.position().add(0, yOffset, 0),
                contraption.getDeltaMovement(),
                contraption.getYRot(),
                contraption.getXRot(),
                (newEntity) -> {
                    if (newEntity instanceof AbstractContraptionEntity newContraption) {
                        newContraption.getContraption().readNBT(ag, nbt, true);
                        player.getServer().tell(new TickTask(player.getServer().getTickCount() + 20, () -> {
                            Entity targVehicle = finalVehicle.get();
                            if (targVehicle != null) {
                                newContraption.startRiding(targVehicle);

                                int index = 0;
                                for (Entity pass : passengers) {
                                    newContraption.addSittingPassenger(pass, index);
                                    index++;
                                }

                                newContraption.getContraption().invalidateClientContraptionStructure();
                                newContraption.getContraption().invalidateClientContraptionChildren();
                                newContraption.getContraption().invalidateColliders();
                                newContraption.getContraption().resetClientContraption();
                            } else {
                                for (Entity pass : List.copyOf(newContraption.getPassengers())) {
                                    pass.stopRiding();
                                }

                                newContraption.stopRiding();
                                newContraption.discard();
                            }
                        }));
                    }
                }
        ));

        if (cVehicle != null) {
            cVehicle.changeDimension(new DimensionTransition(
                    target,
                    cVehicle.position().add(0, yOffset, 0),
                    cVehicle.getDeltaMovement(),
                    cVehicle.getYRot(),
                    cVehicle.getXRot(),
                    finalVehicle::set
            ));
        }
    }
}
