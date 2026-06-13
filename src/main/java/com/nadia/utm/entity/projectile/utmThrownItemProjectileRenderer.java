package com.nadia.utm.entity.projectile;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.utm;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@ForceLoad
public class utmThrownItemProjectileRenderer {

    static {
        utmEventHost.register(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerEntityRenderer(utmEntities.ARID_STONE.get(), ThrownItemRenderer::new));
    }
}
