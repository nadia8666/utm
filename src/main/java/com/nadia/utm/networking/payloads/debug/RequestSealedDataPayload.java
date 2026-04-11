package com.nadia.utm.networking.payloads.debug;

import com.nadia.utm.networking.PacketDef;
import com.nadia.utm.networking.utmCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

public record RequestSealedDataPayload(ChunkPos pos) implements CustomPacketPayload {
    public static final Type<RequestSealedDataPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "request_sealed_data"));

    public static final StreamCodec<FriendlyByteBuf, RequestSealedDataPayload> CODEC = StreamCodec.composite(
            utmCodecs.CHUNK_POS_STREAM, RequestSealedDataPayload::pos,
            RequestSealedDataPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketDef<RequestSealedDataPayload> DEF = new PacketDef<>(TYPE, CODEC);
}