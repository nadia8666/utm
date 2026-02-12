package com.nadia.utm.client.renderer;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import net.minecraft.Util;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.GLINT_DEFAULT;
import static net.minecraft.client.renderer.RenderStateShard.*;

public class utmRenderTypes {
    private static class Shards {
        public static TransparencyStateShard OVERLAY_TRANSPARENCY = new TransparencyStateShard("utm_translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });

        public static TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard("utm_additive_transparency", () -> {
            RenderSystem.enableBlend(); // essentially just additive but include alpha channel
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });

        public static TexturingStateShard ITEM_TEXTURING = new TexturingStateShard("utm_glint_item_tex", () -> {
            RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

            RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
            RenderSystem.setTextureMatrix(new Matrix4f().scale(8).rotateZ(0.17453292F));
        }, RenderSystem::resetTextureMatrix);

        public static TexturingStateShard ARMOR_TEXTURING = new TexturingStateShard("utm_glint_armor_tex", () -> {
            RenderSystem.setShaderTexture(0, utmGlintContainer.GLINT_LOCATION.THREAD.get());

            RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
            RenderSystem.setTextureMatrix(new Matrix4f().scale(0.16f).rotateZ(0.17453292F));
        }, RenderSystem::resetTextureMatrix);
    }

    public static final Supplier<RenderType> OVERLAY_GLINT_ITEM = Suppliers.memoize(() -> RenderType.create("utm_glint_o_item",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_OVERLAY))
                    .setTextureState(new RenderStateShard.TextureStateShard(GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(Shards.OVERLAY_TRANSPARENCY)
                    .setTexturingState(Shards.ITEM_TEXTURING)
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> ADDITIVE_GLINT_ITEM = Suppliers.memoize(() -> RenderType.create("utm_glint_a_item",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_ADDITIVE))
                    .setTextureState(new RenderStateShard.TextureStateShard(GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(Shards.ADDITIVE_TRANSPARENCY)
                    .setTexturingState(Shards.ITEM_TEXTURING)
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> OVERLAY_GLINT_ENTITY = Suppliers.memoize(() -> RenderType.create("utm_glint_o_entity",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_OVERLAY))
                    .setTextureState(new RenderStateShard.TextureStateShard(GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(Shards.OVERLAY_TRANSPARENCY)
                    .setTexturingState(Shards.ARMOR_TEXTURING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> ADDITIVE_GLINT_ENTITY = Suppliers.memoize(() -> RenderType.create("utm_glint_a_entity",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_ADDITIVE))
                    .setTextureState(new RenderStateShard.TextureStateShard(GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(Shards.ADDITIVE_TRANSPARENCY)
                    .setTexturingState(Shards.ARMOR_TEXTURING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> OVERLAY_GLINT_ENTITY_BACKTANK = Suppliers.memoize(() -> RenderType.create("utm_glint_o_entity_backtank",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_OVERLAY))
                    .setTextureState(new RenderStateShard.TextureStateShard(GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(Shards.OVERLAY_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setTexturingState(Shards.ARMOR_TEXTURING)
                    .createCompositeState(false)
    ));

    public static final Supplier<RenderType> ADDITIVE_GLINT_ENTITY_BACKTANK = Suppliers.memoize(() -> RenderType.create("utm_glint_a_entity_backtank",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.GLINT_ADDITIVE))
                    .setTextureState(new RenderStateShard.TextureStateShard(GLINT_DEFAULT, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(Shards.ADDITIVE_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setTexturingState(Shards.ARMOR_TEXTURING)
                    .createCompositeState(false)
    ));

    public static final Function<ResourceLocation, RenderType> EMISSIVE_ARMOR_CUTOUT = Util.memoize((id) -> RenderType.create("utm_emissive_armor_cutout",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    1536,
                    true,
                    false,
                    RenderType.CompositeState.builder()
                            .setShaderState(new RenderStateShard.ShaderStateShard(() -> utmShaders.EMISSIVE_ARMOR_CUTOUT))
                            .setTextureState(new RenderStateShard.TextureStateShard(id, false, false))
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .setOverlayState(OVERLAY)
                            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                            .setDepthTestState(EQUAL_DEPTH_TEST)
                            .createCompositeState(true)
    ));

    private static final ResourceLocation PARTICLE_ATLAS = ResourceLocation.withDefaultNamespace("textures/atlas/particles.png");
    public static final Supplier<ParticleRenderType> EMISSIVE_PARTICLE = Suppliers.memoize(() -> new ParticleRenderType() {
        @Override
        public BufferBuilder begin(@NotNull Tesselator tess, @NotNull TextureManager texManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, PARTICLE_ATLAS);

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.depthMask(false);

            return tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "CUSTOM";
        }
    });
}
