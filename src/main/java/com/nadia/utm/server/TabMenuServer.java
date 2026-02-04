package com.nadia.utm.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nadia.utm.networking.TabLayerPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
                    } catch (Exception ignored) {}
                }
            }
        }

        if (!Files.exists(SAVE_PATH)) return;
        try (Reader reader = Files.newBufferedReader(SAVE_PATH)) {
            Type type = new com.google.gson.reflect.TypeToken<Map<UUID, String>>(){}.getType();
            Map<UUID, String> loaded = GSON.fromJson(reader, type);
            if (loaded != null) PLAYER_CACHE.putAll(loaded);
        } catch (Exception ignored) {}
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
        } catch (Exception ignored) {}
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
}