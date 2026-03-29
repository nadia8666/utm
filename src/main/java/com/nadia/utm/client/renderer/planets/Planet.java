package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
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
        float brightness = getBrightness(time, event.getPartialTick().getGameTimeDeltaTicks());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);

        var color = RenderSystem.getShaderColor();
        RenderSystem.setShaderColor(1, 1, 1, 1.0f);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        poseStack.pushPose();

        Quaternionf camRot = mc.gameRenderer.getMainCamera().rotation();
        poseStack.mulPose(camRot.invert());

        transform(poseStack);

        Matrix4f matrix4f = poseStack.last().pose();
        bufferBuilder.addVertex(matrix4f, -SIZE, DISTANCE, -SIZE).setUv(0.0F, 0.0F).setColor(brightness, brightness, brightness, 1f);
        bufferBuilder.addVertex(matrix4f, SIZE, DISTANCE, -SIZE).setUv(1.0F, 0.0F).setColor(brightness, brightness, brightness, 1f);
        bufferBuilder.addVertex(matrix4f, SIZE, DISTANCE, SIZE).setUv(1.0F, 1.0F).setColor(brightness, brightness, brightness, 1f);
        bufferBuilder.addVertex(matrix4f, -SIZE, DISTANCE, SIZE).setUv(0.0F, 1.0F).setColor(brightness, brightness, brightness, 1f);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
    }

    protected boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
        if (mc.level == null) return false; // not needed in logic
        return mc.level.dimension().equals(utmDimensions.AG_KEY);
    }

    protected void transform(PoseStack poseStack) {
        poseStack.mulPose(Axis.XP.rotationDegrees(11.2F));
    }

    protected float getBrightness(long time, float partialTick) {
        double pTime = (time % 24000L) + partialTick;
        double ang = (pTime / 24000.0) * 2.0 * Math.PI;
        return (float) ((Math.sin(ang) + 1.0) / 2.0);
    }
}
