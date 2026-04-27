package com.nadia.utm;

import com.nadia.utm.block.GrateBlock;
import com.nadia.utm.client.ponder.utmPonderPlugin;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import com.nadia.utm.client.updater.UpdateToast;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.nadia.utm.updater.AutoUpdater.*;

@Mod(value = utm.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = utm.MODID, value = Dist.CLIENT)
public class utmClient {
    public utmClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        utm.loadClasses(container, "CLIENT");

        RadialWrenchMenu.registerRotationProperty(GrateBlock.VERTICAL_DIRECTION, "Vertical Direction");
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Minecraft.getInstance().getTextureManager().getTexture(utmGlintContainer.GLINT_DEFAULT));

        new utmPonderPlugin().register();
    }

    public static void tryToastPopup() {
        if (ToastTarget) {
            ToastTarget = false;

            CompletableFuture.runAsync(() -> Minecraft.getInstance().getToasts().addToast(new UpdateToast(VersionTarget)), CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS));
        }
    }
}
