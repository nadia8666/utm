package com.nadia.utm.networking.payloads;

import com.nadia.utm.networking.PacketDef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GetOxygenPayload(String id, Integer oxygen) implements CustomPacketPayload {
    public static final Type<GetOxygenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "get_oxygen"));

    public static final StreamCodec<FriendlyByteBuf, GetOxygenPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, GetOxygenPayload::id,
            ByteBufCodecs.VAR_INT, GetOxygenPayload::oxygen,
            GetOxygenPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketDef<GetOxygenPayload> DEF = new PacketDef<>(TYPE, CODEC);
}
