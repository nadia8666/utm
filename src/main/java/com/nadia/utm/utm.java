package com.nadia.utm;

import com.nadia.utm.event.BoundEvent;
import com.nadia.utm.event.utmEvents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.objectweb.asm.Type;
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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Mod(utm.MODID)
public class utm {
    public static final String MODID = "utm";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static String VERSION = "";

    public utm(IEventBus modEventBus, ModContainer modContainer) {
        utmEvents.setup(modEventBus);
        bindEvents(modContainer);

        VERSION = modContainer.getModInfo().getVersion().toString();
        AutoUpdater.CURRENT_VERSION = VERSION;
        modEventBus.addListener(this::commonSetup);

        utmRegistry.registerAll(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(utmRegistry::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    public static void bindEvents(ModContainer container) {
        var scanData = container.getModInfo().getOwningFile().getFile().getScanResult();
        var annotationType = Type.getDescriptor(BoundEvent.class);

        scanData.getAnnotations().stream()
                .filter(data -> annotationType.equals(data.annotationType().getDescriptor()))
                .forEach(data -> {
                    try {
                        Class.forName(data.clazz().getClassName(), true, utmEvents.class.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        utm.LOGGER.error("[UTM] Failed to load class {}, there WILL be problems!", data.clazz().getClassName());
                    }
                });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        AutoUpdater.startAutoUpdateLoop();

        event.enqueueWork(() -> {
            try {
                Path schematicDir = FMLPaths.GAMEDIR.get().resolve("schematics\\uploaded\\SERVER");
                if (!Files.exists(schematicDir)) Files.createDirectories(schematicDir); // is this needed?

                Path targetPath = schematicDir.resolve("incredipak.nbt");
                String sourcePath = "/assets/utm/schematics/incredipak.nbt";

                try (InputStream stream = getClass().getResourceAsStream(sourcePath)) {
                    if (stream != null) Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                utm.LOGGER.warn("[UTM] Failed to save schematics: {}", e.getMessage());
            }
        });

        event.enqueueWork(utmRegistry::lateRegister);
    }

    // dont delete this apprently. this is core to utm working.
    @SuppressWarnings("EmptyMethod")
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public static ResourceLocation key(String path) {
        return ResourceLocation.fromNamespaceAndPath("utm", path);
    }
}
