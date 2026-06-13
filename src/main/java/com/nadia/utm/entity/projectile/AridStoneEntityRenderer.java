package com.nadia.utm.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AridStoneEntityRenderer extends EntityRenderer<AridStoneEntity> {
    protected AridStoneEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AridStoneEntity aridStoneEntity) {
        return null;
    }

}
