package com.nadia.utm.networking.payloads;

import com.nadia.utm.networking.PacketDef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

// sorry cursing is rude
public record jumbo_josh(Vector3f pos, String targetUUID) implements CustomPacketPayload {
    public static final Type<jumbo_josh> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "utm"));

    public static final StreamCodec<FriendlyByteBuf, jumbo_josh> CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, jumbo_josh::pos,
            ByteBufCodecs.STRING_UTF8, jumbo_josh::targetUUID,
            jumbo_josh::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final PacketDef<jumbo_josh> DEF = new PacketDef<>(TYPE, CODEC);

}
