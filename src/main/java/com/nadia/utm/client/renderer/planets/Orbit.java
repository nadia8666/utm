package com.nadia.utm.client.renderer.planets;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public interface Orbit {
    Vector3d getPosition(long worldTime, float partialTicks);

    record Static(Vector3d pos) implements Orbit {
        @Override
        public Vector3d getPosition(long worldTime, float partialTicks) {
            return pos;
        }
    }

    record Circular(Orbit parent, double radius, double speed, Quaternionf planeRotation) implements Orbit {
        @Override
        public Vector3d getPosition(long worldTime, float partialTicks) {
            double time = (worldTime + partialTicks) * speed;
            Vector3f localPos = new Vector3f((float) (Math.cos(time) * radius), 0, (float) (Math.sin(time) * radius));
            Vector3d center = parent != null ? parent.getPosition(worldTime, partialTicks) : new Vector3d();

            localPos.rotate(planeRotation);

            return new Vector3d(localPos).add(center);
        }
    }
}