package com.nadia.utm.networking;

import net.minecraft.world.phys.Vec3;

public class MathHelper {
    public static float pickLargest(float... values) {
        Float min = null;
        for (float f : values) {
            if (min == null || min < f) {
                min = f;
            }
        }
        return min == null ? -1 : min;
    }

    public static float getOversizeScale(Vec3 original, Vec3 clamped) {
        return pickLargest((float) (clamped.x / original.x), (float) (clamped.y / original.y), (float) (clamped.z / original.z));
    }
}
