package com.nadia.utm;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.nadia.utm.registry.utmRegistry;
import com.nadia.utm.updater.AutoUpdater;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(utm.MODID)
public class utm {
    public static final String MODID = "utm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public utm(IEventBus modEventBus, ModContainer modContainer) {
        AutoUpdater.CURRENT_VERSION = modContainer.getModInfo().getVersion().toString();

        modEventBus.addListener(this::commonSetup);

        utmRegistry.registerAll(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(utmRegistry::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        AutoUpdater.startAutoUpdateLoop();
    }

    // dont delete this apprently. this is core to utm working.
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
}
