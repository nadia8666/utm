package com.nadia.utm.mixin.renderer;

import com.nadia.utm.renderer.utmShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Supplier;

@Mixin(value = RenderStateShard.ShaderStateShard.class, remap = false)
public abstract class GlintMixin {
    @ModifyVariable(
            method = "<init>(Ljava/util/function/Supplier;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Supplier<ShaderInstance> utm$replaceGlintSupplier(Supplier<ShaderInstance> original) {
        return () -> {
            ShaderInstance shader = original.get();

            if (shader != null && shader.getName().contains("glint")) {
                return utmShaders.COLORED_GLINT != null ? utmShaders.COLORED_GLINT : shader;
            }

            return shader;
        };
    }
}
