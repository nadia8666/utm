package com.nadia.utm.renderer;

import com.google.common.base.Suppliers;
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

import java.util.function.Supplier;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class utmRenderTypes {
    public static final Supplier<RenderType> OVERLAY_GLINT_ITEM = Suppliers.memoize(() -> RenderType.create("utm_glint_o_item",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_OVERLAY))
                    .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(new TransparencyStateShard("utm_translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .setTexturingState(new RenderStateShard.TexturingStateShard("utm_glint_o_tex", () -> {
                        RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

                        RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
                        RenderSystem.setTextureMatrix(new Matrix4f().scale(8).rotateZ(0.17453292F));
                    }, RenderSystem::resetTextureMatrix))
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> ADDITIVE_GLINT_ITEM = Suppliers.memoize(() -> RenderType.create("utm_glint_a_item",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_ADDITIVE))
                    .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(new TransparencyStateShard("utm_additive_transparency", () -> {
                        RenderSystem.enableBlend(); // essentially just additive but include alpha channel
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .setTexturingState(new RenderStateShard.TexturingStateShard("utm_glint_a_tex", () -> {
                        RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

                        RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
                        RenderSystem.setTextureMatrix(new Matrix4f().scale(8).rotateZ(0.17453292F));
                    }, RenderSystem::resetTextureMatrix))
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> OVERLAY_GLINT_ENTITY = Suppliers.memoize(() -> RenderType.create("utm_glint_o_entity",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_OVERLAY))
                    .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(new TransparencyStateShard("utm_translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .setTexturingState(new RenderStateShard.TexturingStateShard("utm_glint_o_tex", () -> {
                        RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

                        RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
                        RenderSystem.setTextureMatrix(new Matrix4f().scale(0.16f).rotateZ(0.17453292F));
                    }, RenderSystem::resetTextureMatrix))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> ADDITIVE_GLINT_ENTITY = Suppliers.memoize(() -> RenderType.create("utm_glint_a_entity",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_ADDITIVE))
                    .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(new TransparencyStateShard("utm_additive_transparency", () -> {
                        RenderSystem.enableBlend(); // essentially just additive but include alpha channel
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .setTexturingState(new RenderStateShard.TexturingStateShard("utm_glint_a_tex", () -> {
                        RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

                        RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
                        RenderSystem.setTextureMatrix(new Matrix4f().scale(0.16f).rotateZ(0.17453292F));
                    }, RenderSystem::resetTextureMatrix))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    ));
}
