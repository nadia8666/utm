package com.nadia.utm.client.renderer.glint;

import net.minecraft.resources.ResourceLocation;

public class utmGlintContainer {
    public static final ResourceLocation GLINT_DEFAULT = ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint.png");
    public static final ThreadLocal<ResourceLocation> GLINT_CURRENT =
            ThreadLocal.withInitial(() -> GLINT_DEFAULT);

    public static final ThreadLocal<Boolean> GLINT_ADDITIVE = ThreadLocal.withInitial(() -> true);
}
