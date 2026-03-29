package com.nadia.utm.event;

import com.nadia.utm.networking.TabLayerPayload;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.server.TabMenuServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.nadia.utm.registry.attachment.utmAttachments.ENTERED_2313AG;

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
        MinecraftServer server = event.getServer();
        TabMenuServer.loadData(server);
        refreshTabMenuData(server);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        TabMenuServer.saveData();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer server = event.getEntity().getServer();
        if (server == null) return;

        if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.addPlayer(player);
        refreshTabMenuData(server);
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) refreshTabMenuData(player.getServer());
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) refreshTabMenuData(player.getServer());

        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getTo().equals(utmDimensions.AG_KEY)) {
                if (!player.getData(ENTERED_2313AG)) {
                    player.setData(ENTERED_2313AG, true);
                    player.setRespawnPosition(utmDimensions.AG_KEY, player.blockPosition(), player.getYRot(), true, true);
                }
            }
        }
    }

    public static void refreshTabMenuData(MinecraftServer server) {
        TabLayerPayload payload = TabMenuServer.create(server);
        PacketDistributor.sendToAllPlayers(payload);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living && !entity.level().isClientSide) {
            if (entity.level().dimension().equals(utmDimensions.AG_KEY)) {
                var gravity = living.getAttribute(Attributes.GRAVITY);
                if (gravity != null && gravity.getBaseValue() != 0.12) {
                    gravity.setBaseValue(0.12);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        //TODO: see if this can be optimized better idk this feels like a lot for 20/s
        if (event.getEntity() instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            if (server == null) return;

            if (player.getData(ENTERED_2313AG) && !player.serverLevel().dimension().equals(utmDimensions.AG_KEY)) {
                ServerLevel target = server.getLevel(utmDimensions.AG_KEY);
                if (target != null) {
                    int height = target.getHeight(Heightmap.Types.MOTION_BLOCKING, player.blockPosition().getX(), player.blockPosition().getZ());
                    player.teleportTo(target, player.getX(), height + 1, player.getZ(), player.getYRot(), player.getXRot());
                }
            } else if (!player.getData(ENTERED_2313AG) && player.serverLevel().dimension().equals(utmDimensions.AG_KEY)) {
                player.setData(ENTERED_2313AG, true);
                player.setRespawnPosition(utmDimensions.AG_KEY, player.blockPosition(), player.getYRot(), true, true);
            }
        }
    }
}
