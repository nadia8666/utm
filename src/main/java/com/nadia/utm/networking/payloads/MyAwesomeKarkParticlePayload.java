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
public record MyAwesomeKarkParticlePayload(Vector3f pos, Vector3f dir, double xOff, double yOff) implements CustomPacketPayload {
    public static final Type<MyAwesomeKarkParticlePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "fuck_i_guess"));

    public static final StreamCodec<FriendlyByteBuf, MyAwesomeKarkParticlePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, MyAwesomeKarkParticlePayload::pos,
            ByteBufCodecs.VECTOR3F, MyAwesomeKarkParticlePayload::dir,
            ByteBufCodecs.DOUBLE, MyAwesomeKarkParticlePayload::xOff,
            ByteBufCodecs.DOUBLE, MyAwesomeKarkParticlePayload::yOff,
            MyAwesomeKarkParticlePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketDef<MyAwesomeKarkParticlePayload> DEF = new PacketDef<>(TYPE, CODEC);

}
