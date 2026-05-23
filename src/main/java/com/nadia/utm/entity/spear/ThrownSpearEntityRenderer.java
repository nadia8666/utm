package com.nadia.utm.entity.spear;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.model.PartialEntityModelRenderer;
import com.nadia.utm.registry.model.utmModels;
import com.nadia.utm.util.PoseUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
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
            ms.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
            if (entity.isFlying())
                entity.LAST_ROTATION = ((entity.level().getGameTime() + partialTicks) * 25) % 360;

            ms.mulPose(Axis.ZP.rotationDegrees(entity.LAST_ROTATION));

            ms.translate(0, 0, -0.25);
        }).run(() -> PartialEntityModelRenderer.render(ms,
                utmModels.THROWING_SPEAR_MODELS.getOrDefault(entity.getModel(), utmModels.COPPER_THROWING_SPEAR).get(),
                renderType(entity), buffer, packedLight, 0)).pop();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownSpearEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    public RenderType renderType(ThrownSpearEntity entity) {
        return RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
    }

    static {
        utmEventHost.register(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerEntityRenderer(utmEntities.THROWN_SPEAR.get(), ThrownSpearEntityRenderer::new));
    }
}
