package com.nadia.utm.networking.payloads;

import com.nadia.utm.networking.PacketDef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

// sorry cursing is rude
public record LesserAtkCooldownPayload(Vector3f pos, String targetUUID, int delay) implements CustomPacketPayload {
    public static final Type<LesserAtkCooldownPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "lesseratkpayload"));

    public static final StreamCodec<FriendlyByteBuf, LesserAtkCooldownPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, LesserAtkCooldownPayload::pos,
            ByteBufCodecs.STRING_UTF8, LesserAtkCooldownPayload::targetUUID,
            ByteBufCodecs.INT, LesserAtkCooldownPayload::delay,
            LesserAtkCooldownPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final PacketDef<LesserAtkCooldownPayload> DEF = new PacketDef<>(TYPE, CODEC);

}
