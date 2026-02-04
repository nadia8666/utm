package com.nadia.utm;

import com.nadia.utm.client.UpdateToast;
import com.nadia.utm.registry.ui.utmMenus;
import com.nadia.utm.renderer.utmRenderTypes;
import com.nadia.utm.ui.glint.GlintScreen;
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
