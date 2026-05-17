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
public record Sword2AttackPayload(Vector3f pos) implements CustomPacketPayload {
    public static final Type<Sword2AttackPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "i_will_still_call_it_the_gayload_just_for_you"));

    public static final StreamCodec<FriendlyByteBuf, Sword2AttackPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, Sword2AttackPayload::pos,
            Sword2AttackPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final PacketDef<Sword2AttackPayload> DEF = new PacketDef<>(TYPE, CODEC);

}
