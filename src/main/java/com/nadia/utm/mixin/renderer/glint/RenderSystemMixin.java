package com.nadia.utm.mixin.renderer.glint;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.nadia.utm.renderer.glint.utmGlintContainer.GLINT_CURRENT;

@Mixin(value = RenderSystem.class, remap = false)
public class RenderSystemMixin {
    private static final ResourceLocation VANILLA_GLINT = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");

    @ModifyVariable(
            method = "setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static ResourceLocation utm$overrideGlintTexture(ResourceLocation original) {
        if (original.equals(VANILLA_GLINT)) {
            var texture = GLINT_CURRENT.get();
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.texParameter(3553, 10242, 10497); // GL_REPEAT
            RenderSystem.texParameter(3553, 10243, 10497); // GL_REPEAT
            return texture;
        }
        return original;
    }
}