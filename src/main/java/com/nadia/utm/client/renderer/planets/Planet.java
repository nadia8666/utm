package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Planet {
    public final float DISTANCE;
    public final float SIZE;
    public final ResourceLocation TEXTURE;

    public Planet(float distance, float size, ResourceLocation texture) {
        DISTANCE = distance;
        SIZE = size;
        TEXTURE = texture;

        PlanetRenderer.PLANET_REGISTRY.add(this);
    }

    public void onRenderSky(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        if (!shouldPass(event, mc)) return;

        PoseStack poseStack = event.getPoseStack();

        long time = mc.level.dayTime() % 24000;
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
        buffer.addVertex(matrix, -SIZE, DISTANCE, -SIZE).setUv(0.0F, 0.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);
        buffer.addVertex(matrix, SIZE, DISTANCE, -SIZE).setUv(1.0F, 0.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);
        buffer.addVertex(matrix, SIZE, DISTANCE, SIZE).setUv(1.0F, 1.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);
        buffer.addVertex(matrix, -SIZE, DISTANCE, SIZE).setUv(0.0F, 1.0F).setColor(brightness[0], brightness[1], brightness[2], alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        poseStack.popPose();
        RenderSystem.disableBlend();
    }

    public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
        if (mc.level == null) return false; // not needed in logic
        return mc.level.dimension().equals(utmDimensions.AG_KEY);
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
}
