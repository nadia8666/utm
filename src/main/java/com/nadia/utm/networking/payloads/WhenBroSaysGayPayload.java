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
public record WhenBroSaysGayPayload(Vector3f pos) implements CustomPacketPayload {
    public static final Type<WhenBroSaysGayPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "gayload"));

    public static final StreamCodec<FriendlyByteBuf, WhenBroSaysGayPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, WhenBroSaysGayPayload::pos,
            WhenBroSaysGayPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final PacketDef<WhenBroSaysGayPayload> DEF = new PacketDef<>(TYPE, CODEC);

}
