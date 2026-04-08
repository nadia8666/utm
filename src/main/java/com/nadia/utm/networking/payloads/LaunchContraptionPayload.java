package com.nadia.utm.networking.payloads;

import com.nadia.utm.networking.PacketDef;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record LaunchContraptionPayload(int i) implements CustomPacketPayload{
    public static final Type<LaunchContraptionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "launch_contraption"));

    public static final StreamCodec<ByteBuf, LaunchContraptionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LaunchContraptionPayload::i,
            LaunchContraptionPayload::new
    );

    @Override
    public @NotNull Type<LaunchContraptionPayload> type() {
        return TYPE;
    }

    public static PacketDef<LaunchContraptionPayload> DEF = new PacketDef<>(TYPE, STREAM_CODEC);
}
