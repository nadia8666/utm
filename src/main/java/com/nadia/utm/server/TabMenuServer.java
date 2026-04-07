package com.nadia.utm.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nadia.utm.event.BoundEvent;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.networking.TabLayerPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@BoundEvent
public class TabMenuServer {
    private static final Map<UUID, String> PLAYER_CACHE = new HashMap<>();
    private static final Path SAVE_PATH = Path.of("world/utm_tab_history.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadData(MinecraftServer server) {
        PLAYER_CACHE.clear();

        File folder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    try {
                        String uuidStr = file.getName().replace(".dat", "");
                        UUID uuid = UUID.fromString(uuidStr);
                        ServerPlayer player = server.getPlayerList().getPlayer(uuid);

                        PLAYER_CACHE.put(uuid, player != null ? player.getGameProfile().getName() : uuidStr);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (!Files.exists(SAVE_PATH)) return;
        try (Reader reader = Files.newBufferedReader(SAVE_PATH)) {
            Type type = new com.google.gson.reflect.TypeToken<Map<UUID, String>>() {
            }.getType();
            Map<UUID, String> loaded = GSON.fromJson(reader, type);
            if (loaded != null) PLAYER_CACHE.putAll(loaded);
        } catch (Exception ignored) {
        }
    }

    public static void addPlayer(ServerPlayer player) {
        PLAYER_CACHE.put(player.getUUID(), player.getGameProfile().getName());
    }

    public static void saveData() {
        try {
            Files.createDirectories(SAVE_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(SAVE_PATH)) {
                GSON.toJson(PLAYER_CACHE, writer);
            }
        } catch (Exception ignored) {
        }
    }

    public static TabLayerPayload create(MinecraftServer server) {
        List<TabLayerPayload.PlayerData> data = new ArrayList<>();

        PLAYER_CACHE.forEach((uuid, name) -> {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            boolean online = player != null;

            data.add(new TabLayerPayload.PlayerData(
                    uuid,
                    name,
                    online,
                    online ? (player.getHealth() + player.getAbsorptionAmount()) : 0,
                    online ? player.getMaxHealth() : 0,
                    online ? player.level().dimension().location().toString() : "???",
                    online ? player.connection.latency() : 0));
        });

        data.sort((a, b) -> {
            if (a.online() != b.online()) return a.online() ? -1 : 1;
            return a.name().compareToIgnoreCase(b.name());
        });

        return new TabLayerPayload(data);
    }

    public static void refresh(MinecraftServer server) {
        TabLayerPayload payload = TabMenuServer.create(server);
        PacketDistributor.sendToAllPlayers(payload);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 == 0) {
            TabMenuServer.refresh(event.getServer());
        }
    }

    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        TabMenuServer.loadData(server);
        TabMenuServer.refresh(server);
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        TabMenuServer.saveData();
    }

    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer server = event.getEntity().getServer();
        if (server == null) return;

        if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.addPlayer(player);
        TabMenuServer.refresh(server);
    }

    public static void onEntityDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.refresh(player.getServer());
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.refresh(player.getServer());
    }

    static {
        utmEvents.register(ServerTickEvent.Post.class, TabMenuServer::onServerTick);
        utmEvents.register(ServerStartingEvent.class, TabMenuServer::onServerStarting);
        utmEvents.register(ServerStoppingEvent.class, TabMenuServer::onServerStopping);
        utmEvents.register(PlayerEvent.PlayerLoggedInEvent.class, TabMenuServer::onPlayerJoin);
        utmEvents.register(LivingDamageEvent.Post.class, TabMenuServer::onEntityDamage);
        utmEvents.register(PlayerEvent.PlayerChangedDimensionEvent.class, TabMenuServer::onDimensionChange);
    }

}