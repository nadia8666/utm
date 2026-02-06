package com.nadia.utm.server;

import com.nadia.utm.block.BlockChunkLoaderBlock;
import com.nadia.utm.block.entity.ChunkLoaderBlockEntity;
import com.nadia.utm.utm;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = "utm")
public class ChunkLoadHandler {
    public static final TicketController CONTROLLER = new TicketController(
            ResourceLocation.fromNamespaceAndPath("utm", "chunk_loader"),
            (level, helper) -> {
                helper.getBlockTickets().forEach((pos, ticketSet) -> {
                    if (!(level.getBlockState(pos).getBlock() instanceof BlockChunkLoaderBlock)) {
                        ticketSet.ticking().forEach(chunk -> helper.removeTicket(pos, chunk, true));
                        ticketSet.nonTicking().forEach(chunk -> helper.removeTicket(pos, chunk, false));
                    }
                });
            }
    );

    public static final Map<BlockPos, ChunkLoaderBlockEntity> LOADERS = new HashMap<>();

    public static void addLoader(BlockPos pos, ChunkLoaderBlockEntity entity) {
        LOADERS.put(pos, entity);
    }

    public static void removeLoader(BlockPos pos, ChunkLoaderBlockEntity entity) {
        LOADERS.remove(pos, entity);
    }

    @SubscribeEvent
    public static void onRegisterTicketControllers(RegisterTicketControllersEvent event) {
        event.register(CONTROLLER);
        utm.LOGGER.info("[UTM] Registered ticket controller");
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) LOADERS.forEach((ignored, entity) -> {
            if (entity.SOURCE != null && entity.SOURCE == player.getUUID()) {
                entity.setLoaded(player.serverLevel(), true);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) LOADERS.forEach((ignored, entity) -> {
            if (entity.SOURCE != null && entity.SOURCE == player.getUUID()) {
                entity.setLoaded(player.serverLevel(), false);
            }
        });
    }
}
