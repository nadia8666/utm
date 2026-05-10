package com.nadia.utm.networking.payloads;

import com.nadia.utm.networking.PacketDef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record FUCKPayload(Vector3f pos, Vector3f dir, double d0, double d1) implements CustomPacketPayload {
    public static final Type<FUCKPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "fuck"));

    public static final StreamCodec<FriendlyByteBuf, FUCKPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, FUCKPayload::pos,
            ByteBufCodecs.VECTOR3F, FUCKPayload::dir,
            ByteBufCodecs.DOUBLE, FUCKPayload::d0,
            ByteBufCodecs.DOUBLE, FUCKPayload::d1,
            FUCKPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketDef<FUCKPayload> DEF = new PacketDef<>(TYPE, CODEC);

}
