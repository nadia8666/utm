package com.nadia.utm.networking;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record TabLayerPayload(List<PlayerData> players) implements CustomPacketPayload {
    public static final Type<TabLayerPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "tab_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TabLayerPayload> STREAM_CODEC = StreamCodec.composite(
            PlayerData.STREAM_CODEC.apply(ByteBufCodecs.list()), TabLayerPayload::players,
            TabLayerPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record PlayerData(UUID id, String name, boolean online, float health, float maxHealth, String dimension, int ping) {

        public static final StreamCodec<RegistryFriendlyByteBuf, PlayerData> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buf, PlayerData val) {
                UUIDUtil.STREAM_CODEC.encode(buf, val.id);
                ByteBufCodecs.STRING_UTF8.encode(buf, val.name);
                ByteBufCodecs.BOOL.encode(buf, val.online);
                ByteBufCodecs.FLOAT.encode(buf, val.health);
                ByteBufCodecs.FLOAT.encode(buf, val.maxHealth);
                ByteBufCodecs.STRING_UTF8.encode(buf, val.dimension);
                ByteBufCodecs.VAR_INT.encode(buf, val.ping);
            }

            @Override
            public @NotNull PlayerData decode(@NotNull RegistryFriendlyByteBuf buf) {
                return new PlayerData(
                        UUIDUtil.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.STRING_UTF8.decode(buf),
                        ByteBufCodecs.BOOL.decode(buf),
                        ByteBufCodecs.FLOAT.decode(buf),
                        ByteBufCodecs.FLOAT.decode(buf),
                        ByteBufCodecs.STRING_UTF8.decode(buf),
                        ByteBufCodecs.VAR_INT.decode(buf)
                );
            }
        };
    }
}