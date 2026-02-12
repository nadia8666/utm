package com.nadia.utm.mixin.renderer.glint;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.client.renderer.ElytraUtil;
import com.nadia.utm.client.renderer.utmElytraTrimContainer;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.client.renderer.utmShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@Mixin(value = ElytraLayer.class, remap = false)
public abstract class ElytraLayerMixin<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    @Shadow
    @Final
    private ElytraModel<T> elytraModel;

    @Shadow
    public abstract boolean shouldRender(ItemStack stack, T entity);

    @Shadow
    public abstract ResourceLocation getElytraTexture(ItemStack stack, T entity);

    public ElytraLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Unique
    private Map<LivingEntity, Float> utm$trailAccumulator = new WeakHashMap<>();

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void render(
            PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
            CallbackInfo ci
    ) {
        ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (this.shouldRender(itemstack, livingEntity)) {
            ResourceLocation resourcelocation;
            if (livingEntity instanceof AbstractClientPlayer player) {
                PlayerSkin playerskin = player.getSkin();
                if (playerskin.elytraTexture() != null) {
                    resourcelocation = playerskin.elytraTexture();
                } else if (playerskin.capeTexture() != null && player.isModelPartShown(PlayerModelPart.CAPE)) {
                    resourcelocation = playerskin.capeTexture();
                } else {
                    resourcelocation = this.getElytraTexture(itemstack, livingEntity);
                }
            } else {
                resourcelocation = this.getElytraTexture(itemstack, livingEntity);
            }

            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 0.125F);

            this.getParentModel().copyPropertiesTo(this.elytraModel);
            this.elytraModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            this.elytraModel.renderToBuffer(poseStack, buffer.getBuffer(RenderType.armorCutoutNoCull(resourcelocation)), packedLight, OverlayTexture.NO_OVERLAY);

            if (buffer instanceof MultiBufferSource.BufferSource source) {
                var changed = utmElytraTrimContainer.TRIM_TYPE.passUpdate(itemstack, source, false);
                changed = utmElytraTrimContainer.TRIM_COLOR.passUpdate(itemstack, source, changed);
                changed = GLINT_COLOR.passUpdate(itemstack, source, changed);
                changed = GLINT_LOCATION.passUpdate(itemstack, source, changed);
                changed = GLINT_SPEED.passUpdate(itemstack, source, changed);
                changed = GLINT_SCALE.passUpdate(itemstack, source, changed);
                changed = GLINT_ADDITIVE.passUpdate(itemstack, source, changed);

                if (changed) {
                    int color = GLINT_COLOR.THREAD.get();
                    setGlintColor(color != -1 ? color : DEFAULT_COLOR, GLINT_ADDITIVE.THREAD.get() ? utmShaders.GLINT_ADDITIVE : utmShaders.GLINT_OVERLAY);

                    var rgb = utmElytraTrimContainer.TRIM_COLOR.THREAD.get();
                    utmShaders.EMISSIVE_ARMOR_CUTOUT.safeGetUniform("Color")
                            .set(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f, 1.0f);
                }

                var trimType = utmElytraTrimContainer.TRIM_TYPE.THREAD.get();
                var renderType = utmRenderTypes.EMISSIVE_ARMOR_CUTOUT
                        .apply(ResourceLocation.fromNamespaceAndPath("utm",
                                String.format("textures/entity/elytra/trim/%s.png", trimType)));

                if (!Objects.equals(trimType, ""))
                    this.elytraModel.renderToBuffer(poseStack, buffer.getBuffer(renderType), packedLight, OverlayTexture.NO_OVERLAY);
            }

            if (itemstack.hasFoil())
                this.elytraModel.renderToBuffer(poseStack,
                        buffer.getBuffer(GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ENTITY.get() : utmRenderTypes.OVERLAY_GLINT_ENTITY.get()), packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();

            if (!Objects.equals(utmElytraTrimContainer.TRIM_TYPE.THREAD.get(), "") && livingEntity.isFallFlying()) {
                var accumulator = utm$trailAccumulator.getOrDefault(livingEntity, 0f);
                accumulator += Minecraft.getInstance().getTimer().getRealtimeDeltaTicks() * 3;

                while (accumulator > 1) {
                    ElytraUtil.draw3PTrail(
                            livingEntity.level(), poseStack, this.elytraModel, utmElytraTrimContainer.TRIM_COLOR.THREAD.get(), utmElytraTrimContainer.TRIM_TYPE.THREAD.get(), null
                    );
                    accumulator--;
                }
                utm$trailAccumulator.put(livingEntity, accumulator);
            }
        }

        ci.cancel();
    }
}
