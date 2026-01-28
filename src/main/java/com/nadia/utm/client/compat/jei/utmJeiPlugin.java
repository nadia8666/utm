package com.nadia.utm.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class utmJeiPlugin implements IModPlugin {
    public static IJeiRuntime RUNTIME;
    public record ShareJeiItem(ItemStack stack) implements CustomPacketPayload{

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return null;
        }

        @Override
        public ClientboundCustomPayloadPacket toVanillaClientbound() {
            return CustomPacketPayload.super.toVanillaClientbound();
        }

        @Override
        public ServerboundCustomPayloadPacket toVanillaServerbound() {
            return CustomPacketPayload.super.toVanillaServerbound();
        }
    };

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("utm", "jei-plugin");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        IModPlugin.super.onRuntimeAvailable(jeiRuntime);
        RUNTIME = jeiRuntime;
    }
}
