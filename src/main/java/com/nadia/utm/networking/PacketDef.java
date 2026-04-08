package com.nadia.utm.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PacketDef<P extends CustomPacketPayload>(
        CustomPacketPayload.Type<P> type,
        StreamCodec<? super RegistryFriendlyByteBuf, P> codec
) {}