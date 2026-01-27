package com.nadia.utm_updater;

import net.neoforged.neoforgespi.language.IModInfo;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod("utm_updater")
public class utmUpdater {
    public static final Logger LOGGER = LogUtils.getLogger();

    public utmUpdater(IEventBus ignoredModEventBus, ModContainer modContainer) {
        IModInfo info = modContainer.getModInfo();
        String version = info.getVersion().toString();

        try {
            AutoUpdater.CurrentVersion = version;
            AutoUpdater.CheckForUpdate();
        } catch (Exception e) {
            LOGGER.warn("[UTM] Failed to get version: {}", e.getMessage());
        }
    }
}

