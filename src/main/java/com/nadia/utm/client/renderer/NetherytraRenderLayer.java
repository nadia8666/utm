package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import com.nadia.utm.registry.item.tool.utmTools;
import org.jetbrains.annotations.NotNull;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.GLINT_ADDITIVE;

public class NetherytraRenderLayer<T extends AbstractClientPlayer, M extends net.minecraft.client.model.PlayerModel<T>>
        extends ElytraLayer<T, M> {

    private final ElytraModel<T> elytraModel;
    public NetherytraRenderLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer, modelSet);

        this.elytraModel = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
    }

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("utm" , "textures/entity/elytra/netherytra.png");
    private static final ResourceLocation EMISSIVE = ResourceLocation.fromNamespaceAndPath("utm" , "textures/entity/elytra/netherytra_e.png");

    @Override
    public @NotNull ResourceLocation getElytraTexture(@NotNull ItemStack stack, @NotNull T entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(ItemStack stack, T entity) {
        return stack.is(utmTools.NETHERYTRA.get());
    }

    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            T livingEntity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (shouldRender(itemstack, livingEntity)) {
            ResourceLocation resourcelocation;
            if (livingEntity instanceof AbstractClientPlayer abstractclientplayer) {
                PlayerSkin playerskin = abstractclientplayer.getSkin();
                if (playerskin.elytraTexture() != null) {
                    resourcelocation = playerskin.elytraTexture();
                } else if (playerskin.capeTexture() != null && abstractclientplayer.isModelPartShown(PlayerModelPart.CAPE)) {
                    resourcelocation = playerskin.capeTexture();
                } else {
                    resourcelocation = getElytraTexture(itemstack, livingEntity);
                }
            } else {
                resourcelocation = getElytraTexture(itemstack, livingEntity);
            }

            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 0.125F);
            this.getParentModel().copyPropertiesTo(this.elytraModel);
            this.elytraModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            VertexConsumer vertexconsumer = VertexMultiConsumer.create(
                    itemstack.hasFoil() ? buffer.getBuffer(GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ENTITY.get() : utmRenderTypes.OVERLAY_GLINT_ENTITY.get()) : null, // glint
                    buffer.getBuffer(RenderType.entityTranslucentEmissive(EMISSIVE, false)), // emissive
                    buffer.getBuffer(RenderType.armorCutoutNoCull(resourcelocation)) // main
            );

            this.elytraModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }
    }
}
