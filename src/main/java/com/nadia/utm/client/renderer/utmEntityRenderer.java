package com.nadia.utm.client.renderer;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@SuppressWarnings("unchecked")
@ForceLoad(dist = Dist.CLIENT)
public class utmEntityRenderer {
    static {
        utmEvents.register(EntityRenderersEvent.AddLayers.class, event -> {
            for (PlayerSkin.Model skin : event.getSkins())
                if (event.getSkin(skin) instanceof LivingEntityRenderer<?, ?> renderer)
                    if (renderer.getModel() instanceof PlayerModel<?>) {
                        var castedRenderer = (LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) renderer;
                        castedRenderer.addLayer(new NetherytraLayer<>(castedRenderer, event.getEntityModels()));
                    }
        });
    }
}
