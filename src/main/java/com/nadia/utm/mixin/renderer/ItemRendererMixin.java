package com.nadia.utm.mixin.renderer;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.renderer.utmShaders;
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

@Mixin(value = ItemRenderer.class, remap = false)
public abstract class ItemRendererMixin {
    @Unique
    private static final ThreadLocal<Integer> utm$glintColor = ThreadLocal.withInitial(() -> -1);

    @Unique
    private static void utm$setGlintColor(Uniform uniform, int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        uniform.set(r, g, b, 1.0f);
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
            int newColor = stack.getOrDefault(utmDataComponents.GLINT_COLOR.get(), -1);
            int oldColor = utm$glintColor.get();
            if (newColor != oldColor) {
                bufferSource.endBatch();

                var uni = utmShaders.COLORED_GLINT.getUniform("GlintColor");
                if (uni != null) utm$setGlintColor(uni, newColor != -1 ? newColor : 0x8040CC);

                utm$glintColor.set(newColor);
            }
        }
    }
}
