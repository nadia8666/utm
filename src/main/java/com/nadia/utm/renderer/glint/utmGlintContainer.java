package com.nadia.utm.renderer.glint;

import net.minecraft.resources.ResourceLocation;

public class utmGlintContainer {
    public static final ResourceLocation GLINT_DEFAULT = ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint.png");
    public static final ThreadLocal<ResourceLocation> GLINT_CURRENT =
            ThreadLocal.withInitial(() -> GLINT_DEFAULT);
}
