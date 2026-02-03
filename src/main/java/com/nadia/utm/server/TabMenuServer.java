package com.nadia.utm.server;

import com.mojang.authlib.GameProfile;
import com.nadia.utm.networking.TabLayerPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TabMenuServer {
    private static final List<UUID> PLAYER_CACHE = new ArrayList<>();

    public static void scanPlayerHistory(MinecraftServer server) {
        PLAYER_CACHE.clear();
        File folder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    try {
                        String uuidStr = file.getName().replace(".dat", "");
                        PLAYER_CACHE.add(UUID.fromString(uuidStr));
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    public static TabLayerPayload createPayload(MinecraftServer server) {
        List<TabLayerPayload.PlayerData> data = new ArrayList<>();

        for (UUID uuid : PLAYER_CACHE) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            boolean isOnline = player != null;

            String name = isOnline ? player.getGameProfile().getName() :
                    Objects.requireNonNull(server.getProfileCache()).get(uuid).map(GameProfile::getName).orElse("");

            float health = isOnline ? (player.getHealth() + player.getAbsorptionAmount()) : 0;
            float maxHealth = isOnline ? player.getMaxHealth() : 0;
            String dim = isOnline ? player.level().dimension().location().toString() : "";
            int ping = isOnline ? player.connection.latency() : 0;

            data.add(new TabLayerPayload.PlayerData(uuid, name, isOnline, health, maxHealth, dim, ping));
        }

        data.sort((a, b) -> {
            if (a.online() != b.online()) return a.online() ? -1 : 1;
            return a.name().compareToIgnoreCase(b.name());
        });

        return new TabLayerPayload(data);
    }
}