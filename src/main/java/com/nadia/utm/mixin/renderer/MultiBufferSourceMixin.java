package com.nadia.utm.mixin.renderer;

import com.nadia.utm.client.renderer.utmRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.GLINT_ADDITIVE;

@Mixin(value = MultiBufferSource.BufferSource.class, remap = false)
public class MultiBufferSourceMixin {
    @ModifyVariable(method = "getBuffer", at = @At("HEAD"), argsOnly = true)
    private RenderType utm$swapType(RenderType original) {
        if (original == RenderType.armorEntityGlint())
            return GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ENTITY.get() : utmRenderTypes.OVERLAY_GLINT_ENTITY.get();
        return original;
    }
}
