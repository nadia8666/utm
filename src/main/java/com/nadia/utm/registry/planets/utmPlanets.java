package com.nadia.utm.registry.planets;

import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class utmPlanets {
    public static Set<Planet> ALL_PLANETS = new HashSet<>();
    public static Map<ResourceKey<Level>, Planet> KEY_SET = new HashMap<>();
    public static class Planet {
        public Planet() {
            ALL_PLANETS.add(this);
            KEY_SET.put(this.getKey(), this);
        }

        public double getGravity() {
            return 0.08;
        }

        public ResourceKey<Level> getKey() {
            return Level.OVERWORLD;
        }
    }

    public static Planet AG23 = new Planet() {
        @Override
        public double getGravity() {
            return 0.12;
        }

        @Override
        public ResourceKey<Level> getKey() {
            return utmDimensions.AG_KEY;
        }
    };
}
