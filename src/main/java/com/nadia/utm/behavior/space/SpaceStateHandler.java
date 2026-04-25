package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.networking.payloads.LaunchContraptionPayload;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Objects;
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

        AbstractContraptionEntity contraption = null;
        Entity vehicle = player.level().getEntity(payload.id());
        if (vehicle instanceof AbstractContraptionEntity c)
            contraption = c;

        if (contraption == null) return;

        ServerLevel target = Objects.requireNonNull(Objects.requireNonNull(player.getServer()).getLevel(utmDimensions.AG_KEY));
        if (player.level().dimension().equals(utmDimensions.AG_KEY)) {
            contraption.disassemble();

            return;
        }

        List<Entity> passengers = List.copyOf(contraption.getPassengers());

        AABB bounds = contraption.getBoundingBox();
        int minX = Mth.floor(bounds.minX);
        int maxX = Mth.ceil(bounds.maxX);
        int minZ = Mth.floor(bounds.minZ);
        int maxZ = Mth.ceil(bounds.maxZ);

        int highestHeight = -13579;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int surfaceY = Positioning.getSurface(target, x, z);
                if (surfaceY > highestHeight) {
                    highestHeight = surfaceY;
                }
            }
        }

        if (highestHeight == -13579) {
            highestHeight = -63;
            target.setBlock(new BlockPos(contraption.blockPosition().getX(), -64, contraption.blockPosition().getZ()), Blocks.COBBLESTONE.defaultBlockState(), 3);
        }

        double yOffset = Math.min(highestHeight, 319 - (bounds.getYsize())) - contraption.getY();

        AABB targetBounds = bounds.move(0, yOffset, 0);
        int tMinX = Mth.floor(targetBounds.minX);
        int tMaxX = Mth.ceil(targetBounds.maxX);
        int tMinY = Mth.floor(targetBounds.minY);
        int tMaxY = Mth.ceil(targetBounds.maxY);
        int tMinZ = Mth.floor(targetBounds.minZ);
        int tMaxZ = Mth.ceil(targetBounds.maxZ);

        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        for (int x = tMinX; x <= tMaxX; x++) {
            for (int y = tMinY; y <= tMaxY; y++) {
                for (int z = tMinZ; z <= tMaxZ; z++) {
                    mPos.set(x, y, z);
                    BlockState state = target.getBlockState(mPos);

                    if (!state.isAir() && UNMODIFIED_BLOCKS.contains(state.getBlock()) && !state.hasBlockEntity()) {
                        target.setBlock(mPos, Blocks.AIR.defaultBlockState(), 3);
                    }

                    if (y == tMinY && target.getBlockState(mPos).isAir() && target.getBlockState(mPos.below()).isAir()) {
                        target.setBlock(mPos.below(), Blocks.COBBLESTONE.defaultBlockState(), 3);
                    }
                }
            }
        }

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

                        newContraption.getContraption().readNBT(target, nbt, true);
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
                                newContraption.stopRiding();
                                newContraption.discard();
                            }

                            for (Entity pass : List.copyOf(newContraption.getPassengers())) {
                                pass.stopRiding();
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
