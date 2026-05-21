package com.nadia.utm.server;

import com.nadia.utm.block.misc.loader.BlockChunkLoaderBlock;
import com.nadia.utm.block.misc.loader.ChunkLoaderBlockEntity;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import com.nadia.utm.utm;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

@ForceLoad
public class ChunkLoadHandler {
    public static final TicketController CONTROLLER = new TicketController(
            utm.key("chunk_loader"),
            (level, helper) ->
                    helper.getBlockTickets().forEach((pos, ticketSet) -> {
                        if (!(level.getBlockState(pos).getBlock() instanceof BlockChunkLoaderBlock)) {
                            ticketSet.ticking().forEach(chunk -> helper.removeTicket(pos, chunk, true));
                            ticketSet.nonTicking().forEach(chunk -> helper.removeTicket(pos, chunk, false));
                        }
                    })
    );

    public static final Map<BlockPos, ChunkLoaderBlockEntity> LOADERS = new HashMap<>();

    public static void addLoader(BlockPos pos, ChunkLoaderBlockEntity entity) {
        LOADERS.put(pos, entity);
    }

    public static void removeLoader(BlockPos pos, ChunkLoaderBlockEntity entity) {
        LOADERS.remove(pos, entity);
    }

    static {
        utmEventHost.register(RegisterTicketControllersEvent.class, event -> {
            event.register(CONTROLLER);
        });

        utmEventHost.register(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) LOADERS.forEach((ignored, entity) -> {
                if (entity.SOURCE != null && entity.SOURCE == player.getUUID()) {
                    entity.setLoaded(player.serverLevel(), true);
                }
            });
        });

        utmEventHost.register(PlayerEvent.PlayerLoggedOutEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) LOADERS.forEach((ignored, entity) -> {
                if (entity.SOURCE != null && entity.SOURCE == player.getUUID()) {
                    entity.setLoaded(player.serverLevel(), false);
                }
            });
        });
    }
}
