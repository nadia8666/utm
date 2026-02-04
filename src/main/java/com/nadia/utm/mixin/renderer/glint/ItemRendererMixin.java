package com.nadia.utm.mixin.renderer.glint;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.nadia.utm.client.renderer.utmShaders;
import com.nadia.utm.renderer.utmRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@Mixin(value = ItemRenderer.class, remap = false)
public abstract class ItemRendererMixin {
    @Unique
    private float utm$time = 0f;
    @Unique
    private float utm$lastDraw = RenderSystem.getShaderGameTime();

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void utm$onRenderItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay,
            BakedModel model,
            CallbackInfo ci
    ) {
        if (buffer instanceof MultiBufferSource.BufferSource bufferSource) {
            boolean changed;
            boolean timeUpdated = false;

            float time = RenderSystem.getShaderGameTime();
            if (time != utm$lastDraw) {
                utm$time = (utm$time + (time - utm$lastDraw) * 20 * Minecraft.getInstance().options.glintSpeed().get().floatValue()) % 1f;
                utm$lastDraw = time;

                float offset = utm$time * 100000;
                float x = (offset % 110000) / 110000.0f;
                float y = (offset % 30000) / 30000.0f;

                timeUpdated = true;

                utmShaders.GLINT_ADDITIVE.safeGetUniform("ScrollOffset").set(-x, y);
                utmShaders.GLINT_OVERLAY.safeGetUniform("ScrollOffset").set(-x, y);
            }

            changed = GLINT_COLOR.passUpdate(stack, bufferSource, false);
            changed = GLINT_LOCATION.passUpdate(stack, bufferSource, changed);
            changed = GLINT_SPEED.passUpdate(stack, bufferSource, changed);
            changed = GLINT_SCALE.passUpdate(stack, bufferSource, changed);
            changed = GLINT_ADDITIVE.passUpdate(stack, bufferSource, changed);

            if (changed) {
                int color = GLINT_COLOR.THREAD.get();
                setGlintColor(color != -1 ? color : 0x8040CC, GLINT_ADDITIVE.THREAD.get() ? utmShaders.GLINT_ADDITIVE : utmShaders.GLINT_OVERLAY);
            }

            if (timeUpdated && !changed) bufferSource.endBatch();
        }
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer utm$addOverlay(
            MultiBufferSource bufferSource, RenderType renderType, boolean noEntity, boolean withGlint, @Local(argsOnly = true) ItemStack stack
    ) {
        var isAdditive = GLINT_ADDITIVE.THREAD.get();
        return withGlint ?
                VertexMultiConsumer.create(bufferSource.getBuffer(isAdditive ? utmRenderTypes.ADDITIVE_GLINT_ITEM.get() : utmRenderTypes.OVERLAY_GLINT_ITEM.get()), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType);
    }


    @Inject(
            method = "getArmorFoilBuffer",
            at=@At("HEAD"),
            cancellable = true
    )
    private static void utm$addOverlayArmor(
            MultiBufferSource bufferSource, RenderType renderType, boolean hasFoil, CallbackInfoReturnable<VertexConsumer> cir
    ) {
        cir.setReturnValue(hasFoil ? VertexMultiConsumer.create(bufferSource.getBuffer(GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ENTITY.get() : utmRenderTypes.OVERLAY_GLINT_ENTITY.get()), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType));
    }

    @Inject(
            method = "getCompassFoilBuffer",
            at=@At("HEAD"),
            cancellable = true
    )
    private static void utm$addOverlayArmor(
            MultiBufferSource bufferSource, RenderType renderType, PoseStack.Pose pose, CallbackInfoReturnable<VertexConsumer> cir
    ) {
        cir.setReturnValue(
                VertexMultiConsumer.create(new SheetedDecalTextureGenerator(bufferSource.getBuffer(
                        GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ITEM.get() : utmRenderTypes.OVERLAY_GLINT_ITEM.get()
                ), pose, 0.0078125F), bufferSource.getBuffer(renderType))
        );
    }
}
