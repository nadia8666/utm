package com.nadia.utm.networking.payloads.debug;

import com.nadia.utm.behavior.space.SealedChunkData;
import com.nadia.utm.networking.PacketDef;
import com.nadia.utm.networking.utmCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

public record SyncSealedDataPayload(ChunkPos pos, SealedChunkData data) implements CustomPacketPayload {
    public static final Type<SyncSealedDataPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "sync_sealed_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSealedDataPayload> CODEC = StreamCodec.composite(
            utmCodecs.CHUNK_POS_STREAM, SyncSealedDataPayload::pos,
            ByteBufCodecs.fromCodec(SealedChunkData.CODEC), SyncSealedDataPayload::data,
            SyncSealedDataPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketDef<SyncSealedDataPayload> DEF = new PacketDef<>(TYPE, CODEC);
}