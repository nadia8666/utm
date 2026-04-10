package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.server.TabMenuServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static com.nadia.utm.registry.attachment.utmAttachments.ENTERED_2313AG;

@ForceLoad
class Dimension {
    static {
        utmEvents.register(PlayerEvent.PlayerChangedDimensionEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.refresh(player.getServer());

            if (event.getEntity() instanceof ServerPlayer player) {
                if (event.getTo().equals(utmDimensions.AG_KEY)) {
                    if (!player.getData(ENTERED_2313AG)) {
                        player.setData(ENTERED_2313AG, true);
                        player.setRespawnPosition(utmDimensions.AG_KEY, player.blockPosition(), player.getYRot(), true, true);
                    }
                }
            }
        });
    }
}
