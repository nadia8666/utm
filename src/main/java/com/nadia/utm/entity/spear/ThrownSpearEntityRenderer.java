package com.nadia.utm.entity.spear;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.model.PartialEntityModelRenderer;
import com.nadia.utm.registry.model.utmPartialModels;
import com.nadia.utm.util.PoseUtil;
import com.nadia.utm.utm;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@ForceLoad(dist = Dist.CLIENT)
public class ThrownSpearEntityRenderer extends EntityRenderer<ThrownSpearEntity> {
    protected ThrownSpearEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull ThrownSpearEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, ms, buffer, packedLight);

        new PoseUtil(ms).push().run(() -> {
            ms.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
            ms.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        }).run(() -> PartialEntityModelRenderer.render(ms, utmPartialModels.COPPER_THROWING_SPEAR.get(), renderType(entity), buffer, packedLight, 0)).pop();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownSpearEntity entity) {
        return utm.key("textures/entity/copper_throwing_spear.png");
    }

    public RenderType renderType(ThrownSpearEntity entity) {
        return RenderType.entitySolid(getTextureLocation(entity));
    }

    static {
        utmEvents.register(FMLClientSetupEvent.class, event -> EntityRenderers.register(utmEntities.THROWN_SPEAR.get(), ThrownSpearEntityRenderer::new));
    }
}
