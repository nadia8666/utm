package com.nadia.utm.mixin.renderer.glint;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nadia.utm.client.renderer.utmShaders;
import com.nadia.utm.renderer.utmRenderTypes;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
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

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@Mixin(value = ItemRenderer.class, remap = false)
public abstract class ItemRendererMixin {
    @Unique
    private static void utm$setGlintColor(int rgb, ShaderInstance shader) {
        shader.safeGetUniform("GlintColor")
                .set(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f, 1.0f);
        shader.safeGetUniform("UVScale").set(GLINT_SCALE.THREAD.get().x, GLINT_SCALE.THREAD.get().y);
        shader.safeGetUniform("ScrollSpeed").set(GLINT_SPEED.THREAD.get().x, GLINT_SPEED.THREAD.get().y);
    }

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
            float time = RenderSystem.getShaderGameTime();
            if (time != utm$lastDraw) {
                utm$time = (utm$time + (time - utm$lastDraw) * 20 * Minecraft.getInstance().options.glintSpeed().get().floatValue()) % 1f;
                utm$lastDraw = time;

                bufferSource.endBatch();

                float offset = utm$time * 100000;
                float x = (offset % 110000) / 110000.0f;
                float y = (offset % 30000) / 30000.0f;

                utmShaders.GLINT_ADDITIVE.safeGetUniform("ScrollOffset").set(-x, y);
                utmShaders.GLINT_OVERLAY.safeGetUniform("ScrollOffset").set(-x, y);
            }

            boolean refColor = GLINT_COLOR.tryUpdate(stack);
            boolean refTexture = GLINT_LOCATION.tryUpdate(stack);
            boolean refSpeed = GLINT_SPEED.tryUpdate(stack);
            boolean refScale = GLINT_SCALE.tryUpdate(stack);
            boolean refAdditive = GLINT_ADDITIVE.tryUpdate(stack);

            if (refColor || refTexture || refAdditive || refSpeed || refScale) {
                bufferSource.endBatch();

                int color = GLINT_COLOR.THREAD.get();
                utm$setGlintColor(color != -1 ? color : 0x8040CC, GLINT_ADDITIVE.THREAD.get() ? utmShaders.GLINT_ADDITIVE : utmShaders.GLINT_OVERLAY);
            }
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
                VertexMultiConsumer.create(bufferSource.getBuffer(isAdditive ? utmRenderTypes.ADDITIVE_GLINT : utmRenderTypes.OVERLAY_GLINT), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType);
    }
}
