package com.nadia.utm.client.renderer.glint;

import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.renderer.glint.GlintStateContainer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

public class utmGlintContainer {
    public static final ResourceLocation GLINT_DEFAULT = ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint.png");

    public static final GlintStateContainer<ResourceLocation> GLINT_LOCATION = new GlintStateContainer<>(
            utmDataComponents.GLINT_TYPE,
            GLINT_DEFAULT,
            null
    );

    public static final GlintStateContainer<Integer> GLINT_COLOR = new GlintStateContainer<>(
            utmDataComponents.GLINT_COLOR,
            -1,
            null
    );

    public static final GlintStateContainer<Boolean> GLINT_ADDITIVE = new GlintStateContainer<>(
            utmDataComponents.GLINT_ADDITIVE,
            true,
            null
    );

    public static final GlintStateContainer<Vector2f> GLINT_SPEED = new GlintStateContainer<>(
            utmDataComponents.GLINT_SPEED,
            new Vector2f(1,1),
            null
    );

    public static final GlintStateContainer<Vector2f> GLINT_SCALE = new GlintStateContainer<>(
            utmDataComponents.GLINT_SCALE,
            new Vector2f(1,1),
            null
    );
}
