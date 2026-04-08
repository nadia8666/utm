package com.nadia.utm.networking.payloads;

import com.nadia.utm.networking.PacketDef;
import com.nadia.utm.networking.utmCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public record GlintSyncPayload(
        int color,
        boolean additive,
        Vector2f speed,
        Vector2f scale,
        ResourceLocation glintType
) implements CustomPacketPayload {
    public static final Type<GlintSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "glint_sync"));

    public static final StreamCodec<ByteBuf, GlintSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GlintSyncPayload::color,
            ByteBufCodecs.BOOL, GlintSyncPayload::additive,
            utmCodecs.VECTOR2F_STREAM, GlintSyncPayload::speed,
            utmCodecs.VECTOR2F_STREAM, GlintSyncPayload::scale,
            ResourceLocation.STREAM_CODEC, GlintSyncPayload::glintType,
            GlintSyncPayload::new
    );

    @Override
    public @NotNull Type<GlintSyncPayload> type() {
        return TYPE;
    }

    public static PacketDef<GlintSyncPayload> DEF = new PacketDef<>(TYPE, STREAM_CODEC);
}