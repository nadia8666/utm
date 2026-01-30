package com.nadia.utm.mixin.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.nadia.utm.mixin.renderer.utmGlintContainer.GLINT_CURRENT;

@Mixin(value = RenderSystem.class, remap = false)
public class RenderSystemMixin {
    private static final ResourceLocation VANILLA_GLINT = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");

    @ModifyVariable(
            method = "setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0 // The first ResourceLocation argument
    )
    private static ResourceLocation utm$overrideGlintTexture(ResourceLocation original) {
        if (original.equals(VANILLA_GLINT)) {
            ResourceLocation custom = GLINT_CURRENT.get();
            if (custom != null) {
                return custom;
            }
        }
        return original;
    }
}