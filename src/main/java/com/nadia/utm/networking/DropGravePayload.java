package com.nadia.utm.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record DropGravePayload (
        BlockPos blockPos
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DropGravePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("utm", "drop_grave"));

    public static final StreamCodec<ByteBuf, DropGravePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, DropGravePayload::blockPos,
            DropGravePayload::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}