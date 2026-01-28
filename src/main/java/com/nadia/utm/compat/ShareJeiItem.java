package com.nadia.utm.compat;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ShareJeiItem(String displayName) implements CustomPacketPayload {
    public static final Type<ShareJeiItem> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("utm", "share_jei_item_data"));

    public static final StreamCodec<ByteBuf, ShareJeiItem> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ShareJeiItem::displayName,
            ShareJeiItem::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ShareJeiItem data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player instanceof ServerPlayer serverPlayer) {
                MutableComponent message = Component.translatable("showcaseitem.misc.shared_item", new Object[]{player.getName()}).append(data.displayName());
                serverPlayer.server.getPlayerList().getPlayers().forEach((p) -> p.sendSystemMessage(message));
            }
        }).exceptionally(e -> null);
    }
}
