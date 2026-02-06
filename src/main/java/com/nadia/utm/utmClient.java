package com.nadia.utm;

import com.nadia.utm.block.GrateBlock;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import com.nadia.utm.client.ui.GlintScreen;
import com.nadia.utm.client.updater.UpdateToast;
import com.nadia.utm.registry.ui.utmMenus;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.updater.ToastDisplaySignal;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

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
}
