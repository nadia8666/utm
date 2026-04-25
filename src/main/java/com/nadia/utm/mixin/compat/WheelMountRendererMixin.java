package com.nadia.utm.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WheelMountRenderer.class, remap = false)
public class WheelMountRendererMixin {
    @Inject(
            method = "renderSafe(Ldev/ryanhcode/offroad/content/blocks/wheel_mount/WheelMountBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V",
                    shift = At.Shift.AFTER
            )
    )
    private void utm$buffersquared(WheelMountBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci, @Local(name = "vb") LocalRef<VertexConsumer> vb) {
        vb.set(buffer.getBuffer(RenderType.cutoutMipped()));
    }
}