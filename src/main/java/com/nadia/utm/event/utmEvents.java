package com.nadia.utm.event;

import com.nadia.utm.networking.TabLayerPayload;
import com.nadia.utm.server.TabMenuServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "utm")
public class utmEvents {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 == 0) {
            refreshTabMenuData(event.getServer());
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        TabMenuServer.scanPlayerHistory(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer server = event.getEntity().getServer();
        if (server != null) TabMenuServer.scanPlayerHistory(server);
    }

    @SubscribeEvent
    public static void utmOnPlayerDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) refreshTabMenuData(player.getServer());
    }

    @SubscribeEvent
    public static void utmOnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) refreshTabMenuData(player.getServer());
    }

    public static void refreshTabMenuData(MinecraftServer server) {
        TabLayerPayload payload = TabMenuServer.createPayload(server);
        PacketDistributor.sendToAllPlayers(payload);
    }
}
