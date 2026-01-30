package com.nadia.utm.mixin.renderer;

import net.minecraft.resources.ResourceLocation;

public class utmGlintContainer {
    public static final ResourceLocation GLINT_DEFAULT = ResourceLocation.fromNamespaceAndPath("utm", "misc/test_glint");
    public static final ThreadLocal<ResourceLocation> GLINT_CURRENT =
            ThreadLocal.withInitial(() -> GLINT_DEFAULT);
}
