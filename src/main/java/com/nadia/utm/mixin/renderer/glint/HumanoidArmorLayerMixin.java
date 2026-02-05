package com.nadia.utm.mixin.renderer.glint;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import com.nadia.utm.client.renderer.utmShaders;
import com.nadia.utm.renderer.utmRenderTypes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@Mixin(value = HumanoidArmorLayer.class, remap = false, priority = 1100)
public class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
    @Inject(method = "renderGlint(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;)V", at = @At("HEAD"), cancellable = true)
    public void utm$armorGlint(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Model model, CallbackInfo ci) {
        model.renderToBuffer(poseStack, bufferSource.getBuffer(
                utmGlintContainer.GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ENTITY.get() : utmRenderTypes.OVERLAY_GLINT_ENTITY.get()
        ), packedLight, OverlayTexture.NO_OVERLAY);
        ci.cancel();
    }

    @Inject(
            method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z")
    )
    public void utm$armorGlintUpdate(PoseStack poseStack, MultiBufferSource buffer, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci, @Local() ItemStack stack) {
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            boolean changed;
            changed = GLINT_COLOR.passUpdate(stack, bufferSource, false);
            changed = GLINT_LOCATION.passUpdate(stack, bufferSource, changed);
            changed = GLINT_SPEED.passUpdate(stack, bufferSource, changed);
            changed = GLINT_SCALE.passUpdate(stack, bufferSource, changed);
            changed = GLINT_ADDITIVE.passUpdate(stack, bufferSource, changed);

            if (changed) {
                int color = GLINT_COLOR.THREAD.get();
                setGlintColor(color != -1 ? color : 0x8040CC, GLINT_ADDITIVE.THREAD.get() ? utmShaders.GLINT_ADDITIVE : utmShaders.GLINT_OVERLAY);
            }
        }
    }
}
