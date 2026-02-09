package com.nadia.utm.client.renderer.glint;

import com.nadia.utm.registry.data.utmDataComponents;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

public class utmGlintContainer {
    public static final ResourceLocation GLINT_DEFAULT = ResourceLocation.fromNamespaceAndPath("utm", "textures/misc/glint.png");
    public static final int DEFAULT_COLOR = 0x8040CC;

    public static final StackComponentContainer<ResourceLocation> GLINT_LOCATION = new StackComponentContainer<>(
            utmDataComponents.GLINT_TYPE,
            GLINT_DEFAULT
    );

    public static final StackComponentContainer<Integer> GLINT_COLOR = new StackComponentContainer<>(
            utmDataComponents.GLINT_COLOR,
            -1
    );

    public static final StackComponentContainer<Boolean> GLINT_ADDITIVE = new StackComponentContainer<>(
            utmDataComponents.GLINT_ADDITIVE,
            true
    );

    public static final StackComponentContainer<Vector2f> GLINT_SPEED = new StackComponentContainer<>(
            utmDataComponents.GLINT_SPEED,
            new Vector2f(1,1)
    );

    public static final StackComponentContainer<Vector2f> GLINT_SCALE = new StackComponentContainer<>(
            utmDataComponents.GLINT_SCALE,
            new Vector2f(1,1)
    );

    public static void setGlintColor(int rgb, ShaderInstance shader) {
        shader.safeGetUniform("GlintColor")
                .set(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f, 1.0f);
        shader.safeGetUniform("UVScale").set(GLINT_SCALE.THREAD.get().x, GLINT_SCALE.THREAD.get().y);
        shader.safeGetUniform("ScrollSpeed").set(GLINT_SPEED.THREAD.get().x, GLINT_SPEED.THREAD.get().y);
    }
}
