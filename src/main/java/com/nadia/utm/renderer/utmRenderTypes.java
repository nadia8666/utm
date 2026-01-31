package com.nadia.utm.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import com.nadia.utm.client.renderer.utmShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.joml.Matrix4f;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class utmRenderTypes {
    public static final RenderType OVERLAY_GLINT = RenderType.create("utm_glint_o",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_OVERLAY))
                    .setTextureState(new RenderStateShard.TextureStateShard(utmGlintContainer.GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTexturingState(new RenderStateShard.TexturingStateShard("utm_glint_o_tex", () -> {
                        RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

                        RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
                        RenderSystem.setTextureMatrix(new Matrix4f());
                    }, RenderSystem::resetTextureMatrix))
                    .createCompositeState(false)
    );

    public static final RenderType ADDITIVE_GLINT = RenderType.create("utm_glint_a",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_ADDITIVE))
                    .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
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
                    .setTexturingState(new RenderStateShard.TexturingStateShard("utm_glint_a_tex", () -> {
                        RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

                        RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
                        RenderSystem.setTextureMatrix(new Matrix4f());
                    }, RenderSystem::resetTextureMatrix))
                    .createCompositeState(false)
    );
}
