package com.nadia.utm;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforgespi.language.ModFileScanData;
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
import java.util.*;
import java.util.function.Function;

@Mod(utm.MODID)
public class utm {
    public static final String MODID = "utm";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static String VERSION = "";

    public utm(IEventBus bus, ModContainer container) {
        utmEvents.setup(bus);
        loadClasses(container, "COMMON");

        VERSION = container.getModInfo().getVersion().toString();
        AutoUpdater.CURRENT_VERSION = VERSION;
        bus.addListener(this::commonSetup);

        utmRegistry.registerAll(bus);
        NeoForge.EVENT_BUS.register(this);
        bus.addListener(utmRegistry::addCreative);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static final Set<String> LOADED_CLASSES = new HashSet<>();
    public static void loadClasses(ModContainer container, String sideDist) {
        ModFileScanData scan = container.getModInfo().getOwningFile().getFile().getScanResult();
        String annotation = Type.getDescriptor(ForceLoad.class);
        String currentDist = FMLEnvironment.dist.name();

        Function<String, Boolean> validDist = (String dist) -> {
            if (sideDist.equals("CLIENT"))
                return !dist.equals("DEDICATED_SERVER");
            else if (sideDist.equals("DEDICATED_SERVER"))
                return !dist.equals("CLIENT");

            return true;
        };

        for (ModFileScanData.AnnotationData data : scan.getAnnotations()) {
            if (annotation.equals(data.annotationType().getDescriptor())) {
                if (LOADED_CLASSES.contains(data.clazz().getClassName())) continue;
                String dist = "COMMON";

                if (data.annotationData().get("dist") instanceof ModAnnotation.EnumHolder targ)
                    dist = targ.value();

                if (!validDist.apply(dist)) continue;

                try {
                    utm.LOGGER.info("[UTM] Initializing class {}!", data.clazz().getClassName());
                    LOADED_CLASSES.add(data.clazz().getClassName());
                    Class.forName(data.clazz().getClassName(), true, utmEvents.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    utm.LOGGER.error("[UTM] Failed to load class {} on {}, there WILL be problems!", data.clazz().getClassName(), dist);
                }
            }
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        AutoUpdater.startAutoUpdateLoop();

        event.enqueueWork(() -> {
            try {
                Path schematicDir = FMLPaths.GAMEDIR.get().resolve("schematics\\uploaded\\SERVER");
                if (!Files.exists(schematicDir)) Files.createDirectories(schematicDir);

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
