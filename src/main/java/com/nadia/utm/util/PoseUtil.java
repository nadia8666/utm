package com.nadia.utm.util;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * chainable {@link PoseStack}
 */
public record PoseUtil(PoseStack pose) {

    public PoseUtil push() {
        pose.pushPose();
        return this;
    }

    public PoseUtil pop() {
        pose.popPose();
        return this;
    }

    public PoseUtil translate(double x, double y, double z) {
        pose.translate(x, y, z);
        return this;
    }

    public PoseUtil translate(float x, float y, float z) {
        pose.translate(x, y, z);
        return this;
    }

    public PoseUtil scale(float x, float y, float z) {
        pose.scale(x, y, z);
        return this;
    }

    public PoseUtil rotate(Quaternionf quaternion) {
        pose.mulPose(quaternion);
        return this;
    }

    public PoseUtil rotateAround(Quaternionf quaternion, float x, float y, float z) {
        pose.rotateAround(quaternion, x, y, z);
        return this;
    }

    public PoseUtil mul(Matrix4f matrix) {
        pose.mulPose(matrix);
        return this;
    }

    public PoseUtil identity() {
        pose.setIdentity();
        return this;
    }

    public PoseStack.Pose last() {
        return pose.last();
    }

    public PoseStack unwrap() {
        return pose;
    }

    /***
     * runs code inline to let you chain `before.run(() -> target).after`
     * @return pose
     */
    public PoseUtil run(Runnable runnable) {
        runnable.run();
        return this;
    }
}