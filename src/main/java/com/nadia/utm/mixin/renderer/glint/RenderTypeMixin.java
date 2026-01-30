package com.nadia.utm.mixin.renderer.glint;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import com.nadia.utm.client.renderer.utmShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

import static net.minecraft.client.renderer.RenderStateShard.*;

@Mixin(RenderType.class)
public class RenderTypeMixin {
    @Shadow public static RenderType GLINT;
    @Unique
    private static Supplier<RenderType> OVERLAY_GLINT = () -> RenderType.create("utm_glint_o",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.COLORED_GLINT))
                    .setTextureState(new RenderStateShard.TextureStateShard(utmGlintContainer.GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .createCompositeState(false)
    );

    @Unique
    private static Supplier<RenderType> ADDITIVE_GLINT = () -> RenderType.create("utm_glint_a",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.COLORED_GLINT))
                    .setTextureState(new RenderStateShard.TextureStateShard(utmGlintContainer.GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(new TransparencyStateShard("additive_transparency", () -> {
                        RenderSystem.enableBlend(); // essentially just additive but include alpha channel
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .setTexturingState(GLINT_TEXTURING)
                    .createCompositeState(false)
    );

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void utm$init(CallbackInfo ci) {
        RenderType.GLINT = ADDITIVE_GLINT.get();
    }
}
