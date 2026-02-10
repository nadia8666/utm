package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.particle.ColorParticleOptions;
import com.nadia.utm.registry.particle.utmParticles;
import com.nadia.utm.utm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ElytraUtil {
    public static void draw3PTrail(Level level, PoseStack poseStack, ElytraModel<?> model, int color, String type) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);
        model.leftWing.translateAndRotate(poseStack);
        Vector4f leftTip = new Vector4f(-10.0F / 16.0F, 20.0F / 16.0F, 0.125F, 1.0F);
        Matrix4f leftMatrix = poseStack.last().pose();
        leftTip = leftTip.mul(leftMatrix);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);
        model.rightWing.translateAndRotate(poseStack);
        Vector4f rightTip = new Vector4f(10.0F / 16.0F, 20.0F / 16.0F, 0.125F, 1.0F);
        Matrix4f rightMatrix = poseStack.last().pose();
        rightTip = rightTip.mul(rightMatrix);
        poseStack.popPose();

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float r, g, b;
        r = ((color >> 16) & 0xFF) / 255f;
        g = ((color >> 8) & 0xFF) / 255f;
        b = (color & 0xFF) / 255f;

        spawnTrail(
                level, type, r, g, b,
                cam.x + leftTip.x(), cam.y + leftTip.y(), cam.z + leftTip.z(),
                cam.x + rightTip.x(), cam.y + rightTip.y(), cam.z + rightTip.z()
        );
    }

    public static void spawnTrail(Level level, String type, float r, float g, float b, double lx, double ly, double lz, double rx, double ry, double rz) {
        try {
            var targetType = utmParticles.getFromString(type);
            if (targetType == null) throw new Exception("[UTM] Unable to find target particle for " + type);

            level.addParticle(new ColorParticleOptions(targetType.get(), r, g, b), lx, ly, lz, 0, 0, 0);
            level.addParticle(new ColorParticleOptions(targetType.get(), r, g, b), rx, ry, rz, 0, 0, 0);
        } catch (Exception e) {
            utm.LOGGER.error(e.getMessage());
        }
    }
}
