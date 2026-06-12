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
public class ThrownAridTridentRenderer extends EntityRenderer<ThrownAridTrident> {
    protected ThrownAridTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    // separate entity model and item model. its easier
    // make your item model have the ovveride for item hotbar thing like the real trident. then overrides for charging. right.
    // then make the entity have the same json as your like. literally the same json or something as the item model but instead have the activated texture. boom
    // see if you can make the unactivated texture display underneath the activated one and then make the activated one emissive. also maybe you should add a spot
    // at the base of the trident that is emissive so it looks cooler. also maybe give it a particle trail.
    // also fix the offset of the crit particles on the sabel and such because those ar ebroekn and offset idk how to make a random offset
    @Override
    public void render(@NotNull ThrownAridTrident entity, float entityYaw, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, ms, buffer, packedLight);

        new PoseUtil(ms).push().run(() -> {
            ms.translate(-0.5, -1.6, -0.5);


            ms.mulPose(Axis.YP.rotationDegrees(90+Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
            ms.mulPose(Axis.ZN.rotationDegrees(-90+Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
          //  ms.mulPose(Axis.ZP.rotationDegrees(90));


        }).run(() -> {
           PartialEntityModelRenderer.render(ms, utmModels.ARID_TRIDENT.get(), renderType(entity), buffer, packedLight, 0);
            PartialEntityModelRenderer.render(ms, utmModels.ARID_TRIDENT_E.get(), renderType(entity), buffer, 0xF000F0, 0);
            //todo: fire when thrown. fire when hits a thing. sounds
            // this incoming particle is huge with many frames but possibly byou could split it up into one Diamodn epr particle
            // and just make it spawn multiple of the same particle at a DELAY
            //PartialEntityModelRenderer.render(a, b, c, d, e, f);
        }).pop();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ThrownAridTrident entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    public RenderType renderType(ThrownAridTrident entity) {
        return RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
    }

    static {
        utmEventHost.register(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerEntityRenderer(utmEntities.THROWN_ARID_TRIDENT.get(), ThrownAridTridentRenderer::new));
    }
}
