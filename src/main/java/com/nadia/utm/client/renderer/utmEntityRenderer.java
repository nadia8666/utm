package com.nadia.utm.client.renderer;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "utm", value= Dist.CLIENT)
public class utmEntityRenderer {


    @SubscribeEvent
    public static void onAddLayer(EntityRenderersEvent.AddLayers event) {
        for(PlayerSkin.Model skin:event.getSkins()) {
            EntityRenderer<? extends Player> rendererRaw=event.getSkin(skin);
            if(rendererRaw instanceof LivingEntityRenderer<?,?> genericRenderer) {
                LivingEntityRenderer<?,?> rawRenderer;
                rawRenderer = genericRenderer;
                if(rawRenderer.getModel() instanceof PlayerModel<?> playerModel) {
                    LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> castedRenderer =
                            (LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) rawRenderer;
                    castedRenderer.addLayer(new NetherytraRenderLayer<>(castedRenderer, event.getEntityModels()));
                }
            }
        }
    }
}
