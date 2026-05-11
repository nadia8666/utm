package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderedPlanet {
    public final ResourceLocation TEXTURE;

    public RenderedPlanet(ResourceLocation texture) {
        TEXTURE = texture;

        PlanetRenderer.RENDERED_PLANET_REGISTRY.add(this);
    }

    public void onRenderSky(long time, Minecraft mc, RenderLevelStageEvent event) {
        if (!shouldPass(event, mc)) return;

        PoseStack poseStack = event.getPoseStack();
        float partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        float[] brightness = getColor(time, partialTicks);
        float alpha = getAlpha(time, partialTicks);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1.0f);

        // transformation
        poseStack.pushPose();

        poseStack.mulPose(new Quaternionf(mc.gameRenderer.getMainCamera().rotation()).invert());
        transform(poseStack, partialTicks);

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, -getSize(), getDistance(), -getSize()).setUv(0.0F, 0.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);
        buffer.addVertex(matrix, getSize(), getDistance(), -getSize()).setUv(1.0F, 0.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);
        buffer.addVertex(matrix, getSize(), getDistance(), getSize()).setUv(1.0F, 1.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);
        buffer.addVertex(matrix, -getSize(), getDistance(), getSize()).setUv(0.0F, 1.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
        return false;
    }

    public void transform(PoseStack poseStack, float partialTicks) {}

    public float[] getColor(long time, float partialTick) {
        double pTime = (time % 24000L) + partialTick;
        double ang = (pTime / 24000.0) * 2.0 * Math.PI;
        float brightness = (float) ((Math.sin(ang) + 1.0) / 2.0);

        return new float[]{brightness, brightness, brightness};
    }

    public float getAlpha(long time, float partialTick) {
        return 1;
    }

    public float getDistance() {
        return 25;
    }

    public float getSize() {
        return 999;
    }
}
