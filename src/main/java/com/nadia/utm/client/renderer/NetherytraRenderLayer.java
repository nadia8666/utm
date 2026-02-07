package com.nadia.utm.client.renderer;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.nadia.utm.registry.item.tool.utmTools;
public class NetherytraRenderLayer<T extends AbstractClientPlayer, M extends net.minecraft.client.model.PlayerModel<T>>
        extends ElytraLayer<T, M> {
    public NetherytraRenderLayer(RenderLayerParent<T, M> p_174493_, EntityModelSet p_174494_) {
        super(p_174493_, p_174494_);
    }

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("utm" , "textures/entity/elytra/netherytra.png");

    @Override
    public ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(ItemStack stack, T entity) {
        return stack.is(utmTools.NETHERYTRA.get());
    }
}
