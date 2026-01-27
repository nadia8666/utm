package com.nadia.utm_updater;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.nadia.utm_updater.AutoUpdater.*;

@Mod(value = "utm_updater", dist = Dist.CLIENT)
@EventBusSubscriber(modid = "utm_updater", value = Dist.CLIENT)
public class utmUpdaterClient {
    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Post event) {
        if (!ToastReady && event.getScreen() instanceof TitleScreen) {
            ToastReady = true;
            utmUpdater.LOGGER.warn("[UTM] Toast unlocked");

            if (ToastTarget) {
                utmUpdater.LOGGER.warn("[UTM] Displaying toast (2)");

                CompletableFuture.runAsync(() -> Minecraft.getInstance().getToasts().addToast(new UpdateToast(VersionTarget)), CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS));

                ToastTarget = false;
            }
        }
    }
}
