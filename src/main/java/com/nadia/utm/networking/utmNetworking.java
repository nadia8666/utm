package com.nadia.utm.networking;

import com.nadia.utm.client.ui.TabMenuLayer;
import com.nadia.utm.compat.GraveInterface;
import com.nadia.utm.gui.glint.GlintMenu;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@EventBusSubscriber(modid = "utm")
public class utmNetworking {
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

        var dropGraveDebounce = new HashMap<UUID, Boolean>();
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

                for (var y = pos.getY()-2; y > level.getMinBuildHeight(); y--) {
                    var targetPos = new BlockPos(pos.getX(), y, pos.getZ());
                    var blockAt = level.getBlockState(targetPos);

                    ((ServerLevel) level).sendParticles(
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

        registrar.playToClient(
                TabLayerPayload.TYPE,
                TabLayerPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        TabMenuLayer.CACHE = payload.players();
                    });
                }
        );
    }
}
