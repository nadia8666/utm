package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import com.nadia.utm.server.TabMenuServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@ForceLoad
class Dimension {
    static {
        utmEventHost.register(PlayerEvent.PlayerChangedDimensionEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.refresh(player.getServer());
        });
    }
}
