package com.nadia.utm.mixin.renderer.glint;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.nadia.utm.renderer.utmRenderTypes.ADDITIVE_GLINT;

@Mixin(RenderType.class)
public class RenderTypeMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void utm$init(CallbackInfo ci) {
        RenderType.GLINT = ADDITIVE_GLINT;
    }
}
