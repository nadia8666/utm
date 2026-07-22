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
        if (clamped.equals(original)) return 1;
        float x = original.x == 0 ? 0 : (float) (original.x / clamped.x);
        float y = original.y == 0 ? 0 : (float) (original.y / clamped.y);
        float z = original.z == 0 ? 0 : (float) (original.z / clamped.z);
        return pickLargest(x, y, z);
    }
}
