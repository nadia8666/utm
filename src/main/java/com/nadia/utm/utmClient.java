package com.nadia.utm;

import com.nadia.utm.block.GrateBlock;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import com.nadia.utm.client.ui.GlintScreen;
import com.nadia.utm.client.updater.UpdateToast;
import com.nadia.utm.particle.ColorParticleProvider;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.particle.utmParticles;
import com.nadia.utm.registry.ui.utmMenus;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.updater.ToastDisplaySignal;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.nadia.utm.updater.AutoUpdater.*;

@Mod(value = utm.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = utm.MODID, value = Dist.CLIENT)
public class utmClient {
    public utmClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        RadialWrenchMenu.registerRotationProperty(GrateBlock.VERTICAL_DIRECTION, "Vertical Direction");
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Minecraft.getInstance().getTextureManager().getTexture(utmGlintContainer.GLINT_DEFAULT));
    }

    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Post event) {
        if (!ToastReady && event.getScreen() instanceof TitleScreen) {
            ToastReady = true;
            tryToastPopup();
        }
    }

    @SubscribeEvent
    public static void onToast(ToastDisplaySignal event) {
        utm.LOGGER.warn("[UTM] Toast recieved");
        tryToastPopup();
    }

    public static void tryToastPopup() {
        utm.LOGGER.warn("[UTM] Toast popup attempted w/ flag {}", ToastTarget);
        if (ToastTarget) {
            ToastTarget = false;

            CompletableFuture.runAsync(() -> Minecraft.getInstance().getToasts().addToast(new UpdateToast(VersionTarget)), CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS));
            utm.LOGGER.warn("[UTM] Toast ran");
        }
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(utmMenus.GLINT_MENU.get(), GlintScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterBuffers(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(utmRenderTypes.ADDITIVE_GLINT_ITEM.get());
        event.registerRenderBuffer(utmRenderTypes.OVERLAY_GLINT_ITEM.get());
        event.registerRenderBuffer(utmRenderTypes.ADDITIVE_GLINT_ENTITY.get());
        event.registerRenderBuffer(utmRenderTypes.OVERLAY_GLINT_ENTITY.get());
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        var type = stack.getOrDefault(utmDataComponents.ELYRA_TRIM_TYPE.get(), "");
        int color = stack.getOrDefault(utmDataComponents.ELYRA_TRIM_COLOR.get(), 0xFFFFFF);

        if (!type.isEmpty()) {
            var material = switch (color) {
                case 0xABABAB -> "Iron";
                case 0xFFBE3D -> "Copper";
                case 0xFFE924 -> "Gold";
                case 0x2F24FF -> "Lapis Lazuli";
                case 0x24FF2B -> "Emerald";
                case 0x24F0FF -> "Diamond";
                case 0x121212 -> "Netherite";
                case 0xEB1515 -> "Redstone";
                case 0xFFFFFF -> "Quartz";
                case 0xB116E0 -> "Amethyst";
                default -> "Unknown";
            };

            event.getToolTip().add(1, Component.literal(" "));

            event.getToolTip().add(1, Component.literal(" " + material + " Material")
                    .withStyle(style -> style.withColor(TextColor.fromRgb(color))));

            event.getToolTip().add(1, Component.translatable("utm.elytra_trim_type." + type)
                    .withStyle(style -> style.withColor(TextColor.fromRgb(color))));

            event.getToolTip().add(1, Component.literal("Upgrade:")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
