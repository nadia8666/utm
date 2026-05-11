package com.nadia.utm.registry.planets;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public interface Orbit {
    double getAreaOfInfluence();
    default double getAreaOfInfluenceSquared() {
        return getAreaOfInfluence() * getAreaOfInfluence();
    };
    Vector3d getPosition(long worldTime, float partialTicks);

    record Static(Vector3d pos, double areaOfInfluence) implements Orbit {
        @Override
        public Vector3d getPosition(long worldTime, float partialTicks) {
            return pos;
        }

        @Override
        public double getAreaOfInfluence() {
            return areaOfInfluence;
        }
    }

    record Circular(@Nullable Orbit parent, double radius, double speed, Quaternionf planeRotation, double areaOfInfluence) implements Orbit {
        @Override
        public Vector3d getPosition(long worldTime, float partialTicks) {
            double time = (worldTime + partialTicks) * speed;
            Vector3f localPos = new Vector3f((float) (Math.cos(time) * radius), 0, (float) (Math.sin(time) * radius));
            Vector3d center = parent != null ? parent.getPosition(worldTime, partialTicks) : new Vector3d();

            localPos.rotate(planeRotation);

            return new Vector3d(localPos).add(center);
        }

        @Override
        public double getAreaOfInfluence() {
            return areaOfInfluence;
        }
    }
}