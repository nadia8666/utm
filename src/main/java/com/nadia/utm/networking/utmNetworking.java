package com.nadia.utm.networking;

import com.nadia.utm.client.ui.TabMenuLayer;
import com.nadia.utm.compat.GraveInterface;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.gui.GlintMenu;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector2f;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@EventBusSubscriber(modid = "utm")
public class utmNetworking {
    private static final Set<Block> UNMODIFIED_BLOCKS = Set.of(
            Blocks.STONE,
            Blocks.COBBLESTONE,
            Blocks.ANDESITE,
            Blocks.STONE_BRICKS,
            Blocks.TUFF,
            Blocks.DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE
    );

    @SubscribeEvent
    public static void registerNetworkingEvents(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(GlintSyncPayload.TYPE, GlintSyncPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof GlintMenu menu) {
                menu.COLOR = payload.color();
                menu.ADDITIVE = payload.additive();
                menu.SPEED = new Vector2f(payload.speed());
                menu.SCALE = new Vector2f(payload.scale());
                menu.TYPE = payload.glintType();

                menu.broadcastChanges();
            }
        }));

        Map<UUID, Boolean> dropGraveDebounce = new HashMap<>();
        registrar.playToServer(DropGravePayload.TYPE, DropGravePayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            UUID uuid = context.player().getUUID();
            if (dropGraveDebounce.get(uuid) != null) return;

            dropGraveDebounce.put(uuid, true);
            CompletableFuture.runAsync(() -> dropGraveDebounce.remove(uuid), CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS));

            var pos = payload.blockPos();
            var level = Objects.requireNonNull(context.player().level().getServer()).getLevel(
                    ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(payload.dimension()))
            );

            if (level == null)
                level = (ServerLevel) context.player().level();

            var target = level.getBlockState(pos);

            if (target.is(Main.GRAVESTONE.get())) {
                for (var i = -2; i > -6; i--) {
                    var targetPos = pos.subtract(new Vec3i(0, i, 0));
                    var blockAt = level.getBlockState(targetPos);

                    if (!blockAt.is(Blocks.AIR)) {
                        return;
                    }
                }

                for (var y = pos.getY() - 2; y > level.getMinBuildHeight(); y--) {
                    var targetPos = new BlockPos(pos.getX(), y, pos.getZ());
                    var blockAt = level.getBlockState(targetPos);

                    level.sendParticles(
                            ParticleTypes.REVERSE_PORTAL,
                            pos.getX() + 0.5, y + 0.5, pos.getZ() + 0.5,
                            3,
                            0.2, 0.2, 0.2,
                            0.05
                    );

                    if (!blockAt.is(Blocks.AIR)) {
                        BlockState state = level.getBlockState(pos);
                        BlockEntity entity = level.getBlockEntity(pos);
                        CompoundTag tag = (entity != null) ? entity.saveWithFullMetadata(level.registryAccess()) : null;

                        level.playSound(null, targetPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.5f, 1f);

                        level.setBlock(targetPos.above(), state, 3);
                        if (tag != null) {
                            BlockEntity newEntity = level.getBlockEntity(targetPos.above());
                            if (newEntity != null) newEntity.loadWithComponents(tag, level.registryAccess());
                        }

                        if (entity instanceof GraveStoneTileEntity grave) {
                            ((GraveInterface) grave).utm$skipItems.set(true);
                        }

                        level.removeBlock(pos, false);

                        if (level.getBlockState(pos.below()).is(Blocks.DIRT)) level.removeBlock(pos.below(), false);

                        break;
                    }
                }
            }
        }));

        registrar.playToClient(TabLayerPayload.TYPE, TabLayerPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() ->
        {
            TabMenuLayer.CACHE = payload.players();
        }));

        registrar.playToServer(LaunchContraptionPayload.TYPE, LaunchContraptionPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            Player player = context.player();
            Entity vehicle = player.getVehicle();
            ServerLevel target = Objects.requireNonNull(Objects.requireNonNull(player.getServer()).getLevel(utmDimensions.AG_KEY));
            if (player.level().dimension().equals(utmDimensions.AG_KEY)) {
                if (vehicle instanceof AbstractContraptionEntity contraption)
                    contraption.disassemble();

                return;
            }
            ;

            if (vehicle instanceof AbstractContraptionEntity contraption) {
                List<Entity> passengers = List.copyOf(contraption.getPassengers());

                AABB bounds = contraption.getBoundingBox();
                int minX = Mth.floor(bounds.minX);
                int maxX = Mth.ceil(bounds.maxX);
                int minZ = Mth.floor(bounds.minZ);
                int maxZ = Mth.ceil(bounds.maxZ);

                int highestHeight = -13579;
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        int surfaceY = utmEvents.getSurface(target, x, z);
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

                CompoundTag nbt = contraption.getContraption().writeNBT(contraption.registryAccess(), false);;

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
        }));
    }
}
