package com.nadia.utm.mixin.renderer.glint;

import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.client.renderer.utmShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@Mixin(value = ElytraLayer.class, remap = false)
public class ElytraLayerMixin {
    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z", shift = At.Shift.AFTER)
    )
    public void utm$armorGlintUpdate(CallbackInfo ci, @Local() ItemStack stack, @Local(argsOnly = true) MultiBufferSource buffer) {
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
