package com.nadia.utm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.nadia.utm.client.UpdateToast;
import static com.nadia.utm.updater.AutoUpdater.ToastReady;
import static com.nadia.utm.updater.AutoUpdater.ToastTarget;
import static com.nadia.utm.updater.AutoUpdater.VersionTarget;

import com.nadia.utm.renderer.utmRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = utm.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = utm.MODID, value = Dist.CLIENT)
public class utmClient {
    public utmClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {}

    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Post event) {
        if (!ToastReady && event.getScreen() instanceof TitleScreen) {
            ToastReady = true;
            utm.LOGGER.warn("[UTM] Toast unlocked");
            tryToastPopup();
        }
    }

    public static void tryToastPopup() {
        if (ToastTarget) {
            utm.LOGGER.warn("[UTM] Displaying toast (2)");

            CompletableFuture.runAsync(() -> Minecraft.getInstance().getToasts().addToast(new UpdateToast(VersionTarget)), CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS));

            ToastTarget = false;
        }
    }

    @SubscribeEvent
    public static void onRegisterBuffers(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(utmRenderTypes.ADDITIVE_GLINT);
        event.registerRenderBuffer(utmRenderTypes.OVERLAY_GLINT);
    }
}
