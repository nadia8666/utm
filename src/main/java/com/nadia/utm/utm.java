package com.nadia.utm;

import com.nadia.utm.updater.AutoUpdater;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(utm.MODID)
public class utm {
    public static final String MODID = "utm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public utm(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        utmRegister.BLOCKS.register(modEventBus);
        utmRegister.ITEMS.register(modEventBus);
        utmRegister.TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(utmRegister::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Update mod
        AutoUpdater.CURRENT_VERSION = modContainer.getModInfo().getVersion().toString();
        AutoUpdater.startAutoUpdate();
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
