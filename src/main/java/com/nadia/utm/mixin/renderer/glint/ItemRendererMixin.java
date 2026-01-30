package com.nadia.utm.mixin.renderer.glint;

import com.mojang.blaze3d.vertex.*;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.client.renderer.utmShaders;
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
    private static final ThreadLocal<Integer> utm$glintColor = ThreadLocal.withInitial(() -> -1);

    @Unique
    private static void utm$setGlintColor(int rgb, boolean isAdditive) {
        var uniform = utmShaders.COLORED_GLINT.safeGetUniform("GlintColor");
        uniform.set(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f, 1.0f);

        utmShaders.COLORED_GLINT.safeGetUniform("IsAdditive").set(isAdditive ? 1 : 0);
    }

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
            var newColor = stack.getOrDefault(utmDataComponents.GLINT_COLOR.get(), -1);
            var oldColor = utm$glintColor.get();

            var newTexture = stack.getOrDefault(utmDataComponents.GLINT_TYPE.get(), GLINT_DEFAULT);
            var oldTexture = GLINT_CURRENT.get();

            var newMode = stack.getOrDefault(utmDataComponents.GLINT_ADDITIVE, true);
            var oldMode = GLINT_ADDITIVE.get();

            if (!newColor.equals(oldColor) || newTexture != oldTexture || newMode != oldMode) {
                bufferSource.endBatch();
                utm$setGlintColor(newColor != -1 ? newColor : 0x8040CC, newMode);

                utm$glintColor.set(newColor);
                GLINT_CURRENT.set(newTexture);
                GLINT_ADDITIVE.set(newMode);
            }
        }
    }
}
