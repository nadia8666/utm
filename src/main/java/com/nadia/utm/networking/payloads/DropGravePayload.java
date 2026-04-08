package com.nadia.utm.networking.payloads;

import com.nadia.utm.compat.GraveInterface;
import com.nadia.utm.networking.PacketDef;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public record DropGravePayload(
        BlockPos blockPos,
        String dimension
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DropGravePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("utm", "drop_grave"));

    public static final StreamCodec<ByteBuf, DropGravePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, DropGravePayload::blockPos,
            ByteBufCodecs.STRING_UTF8, DropGravePayload::dimension,
            DropGravePayload::new
    );

    @Override
    public @NotNull Type<DropGravePayload> type() {
        return TYPE;
    }

    public static PacketDef<DropGravePayload> DEF = new PacketDef<>(TYPE, STREAM_CODEC);

    private static final Map<UUID, Boolean> DEBOUNCE = new HashMap<>();
    public static void drop(DropGravePayload payload, IPayloadContext context) {
        UUID uuid = context.player().getUUID();
        if (DEBOUNCE.get(uuid) != null) return;

        DEBOUNCE.put(uuid, true);
        CompletableFuture.runAsync(() -> DEBOUNCE.remove(uuid), CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS));

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
                        GraveInterface.utm$skipItems.set(true);
                    }

                    level.removeBlock(pos, false);

                    if (level.getBlockState(pos.below()).is(Blocks.DIRT)) level.removeBlock(pos.below(), false);

                    break;
                }
            }
        }
    }
}