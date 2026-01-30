package com.nadia.utm.mixin.renderer.glint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nadia.utm.client.renderer.utmShaders;
import com.nadia.utm.utm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@Mixin(value = ItemRenderer.class, remap = false)
public abstract class ItemRendererMixin {
    @Unique
    private static void utm$setGlintColor(int rgb) {
        utmShaders.COLORED_GLINT.safeGetUniform("GlintColor")
                .set(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f, 1.0f);
        utmShaders.COLORED_GLINT.safeGetUniform("IsAdditive").set(GLINT_ADDITIVE.THREAD.get() ? 1 : 0);
        utmShaders.COLORED_GLINT.safeGetUniform("UVScale").set(GLINT_SCALE.THREAD.get().x, GLINT_SCALE.THREAD.get().y);
        utmShaders.COLORED_GLINT.safeGetUniform("ScrollSpeed").set(GLINT_SPEED.THREAD.get().x, GLINT_SPEED.THREAD.get().y);
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
            var time = RenderSystem.getShaderGameTime();
            if (time != utm$lastDraw) {
                utm$time = (utm$time + (time - utm$lastDraw) * 20 * Minecraft.getInstance().options.glintSpeed().get().floatValue()) % 1f;
                utm$lastDraw = time;

                bufferSource.endBatch();
                utmShaders.COLORED_GLINT.safeGetUniform("ScrollOffset").set(utm$time, utm$time);
            }

            var refColor = GLINT_COLOR.tryUpdate(stack);
            var refTexture = GLINT_LOCATION.tryUpdate(stack);
            var refAdditive = GLINT_ADDITIVE.tryUpdate(stack);
            var refSpeed = GLINT_SPEED.tryUpdate(stack);
            var refScale = GLINT_SCALE.tryUpdate(stack);

            if (refColor || refTexture || refAdditive || refSpeed || refScale) {
                bufferSource.endBatch();
                utm$setGlintColor(GLINT_COLOR.THREAD.get() != -1 ? GLINT_COLOR.THREAD.get() : 0x8040CC);
            }
        }
    }
}
