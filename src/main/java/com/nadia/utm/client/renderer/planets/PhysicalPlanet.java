package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class PhysicalPlanet extends Planet {
    public final Orbit ORBIT;
    public final float SIZE;

    public PhysicalPlanet(ResourceLocation texture, Orbit orbit, float physicalSize) {
        super(texture);
        this.ORBIT = orbit;
        this.SIZE = physicalSize;
    }

    @Override
    public float getSize() {
        return this.SIZE;
    }

    @Override
    public void onRenderSky(long time, Minecraft mc, RenderLevelStageEvent event) {
        if (!shouldPass(event, mc)) return;

        PoseStack poseStack = event.getPoseStack();
        float partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        float[] brightness = getColor(time, partialTicks);
        float alpha = getAlpha(time, partialTicks);

        Vector3d planetPos = ORBIT.getPosition(time, partialTicks);
        Vec3 playerPos = mc.gameRenderer.getMainCamera().getPosition();
        Vector3d offset = new Vector3d();
        planetPos.sub(playerPos.toVector3f(), offset);

        Matrix4f originalProjection = new Matrix4f(RenderSystem.getProjectionMatrix());

        float farPlane = (float) offset.length() + getSize();
        Matrix4f farProjection = new Matrix4f().setPerspective(
                (float) Math.toRadians(mc.options.fov().get().doubleValue()),
                (float) mc.getWindow().getWidth() / (float) mc.getWindow().getHeight(),
                1f, farPlane
        );
        RenderSystem.setProjectionMatrix(farProjection, RenderSystem.getVertexSorting());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1.0f);


        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf(mc.gameRenderer.getMainCamera().rotation()).invert());

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        render(buffer, matrix, (float) offset.x, getSize(), (float) offset.y, (float) offset.z, brightness, alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        poseStack.popPose();
        RenderSystem.setProjectionMatrix(originalProjection, RenderSystem.getVertexSorting());
        RenderSystem.disableBlend();
    }

    private static void render(BufferBuilder buffer, Matrix4f matrix, float x, float s, float y, float z, float[] brightness, float alpha) {
        float r = brightness[0],
                g = brightness[1],
                b = brightness[2];

        buffer.addVertex(matrix, x - s, y - s, z - s).setUv(0, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x - s, y + s, z - s).setUv(0, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y + s, z - s).setUv(1, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y - s, z - s).setUv(1, 0).setColor(r, g, b, alpha);

        buffer.addVertex(matrix, x - s, y - s, z + s).setUv(0, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y - s, z + s).setUv(1, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y + s, z + s).setUv(1, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x - s, y + s, z + s).setUv(0, 1).setColor(r, g, b, alpha);

        buffer.addVertex(matrix, x - s, y - s, z - s).setUv(0, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x - s, y - s, z + s).setUv(1, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x - s, y + s, z + s).setUv(1, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x - s, y + s, z - s).setUv(0, 1).setColor(r, g, b, alpha);

        buffer.addVertex(matrix, x + s, y - s, z - s).setUv(0, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y + s, z - s).setUv(0, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y + s, z + s).setUv(1, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y - s, z + s).setUv(1, 0).setColor(r, g, b, alpha);

        buffer.addVertex(matrix, x - s, y + s, z - s).setUv(0, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x - s, y + s, z + s).setUv(0, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y + s, z + s).setUv(1, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y + s, z - s).setUv(1, 0).setColor(r, g, b, alpha);

        buffer.addVertex(matrix, x - s, y - s, z - s).setUv(0, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y - s, z - s).setUv(1, 0).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x + s, y - s, z + s).setUv(1, 1).setColor(r, g, b, alpha);
        buffer.addVertex(matrix, x - s, y - s, z + s).setUv(0, 1).setColor(r, g, b, alpha);
    }

    @Override
    public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
        return mc.level != null && mc.level.dimension().equals(utmDimensions.SPACE_KEY);
    }
}