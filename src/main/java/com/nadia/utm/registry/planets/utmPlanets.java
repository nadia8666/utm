package com.nadia.utm.registry.planets;

import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class utmPlanets {
    public static Set<Planet> ALL_PLANETS = new HashSet<>();
    public static Map<ResourceKey<Level>, Planet> KEY_SET = new HashMap<>();

    public static class Planet {
        public double GRAVITY;
        public String IDENTIFIER;
        public ResourceKey<Level> KEY;

        public Planet(ResourceKey<Level> key, String identifier, double gravity) {
            KEY = key;
            IDENTIFIER = identifier;
            GRAVITY = gravity;

            ALL_PLANETS.add(this);
            KEY_SET.put(key, this);
        }
    }

    public static Planet EARTH = new Planet(Level.OVERWORLD, "earth", 0.08);
    public static Planet AG23 = new Planet(utmDimensions.AG_KEY, "2313ag", 0.12);

    @Nullable
    public static Planet get(ResourceKey<Level> key) {
        return KEY_SET.get(key);
    }

    @Nullable
    public static Planet get(Level level) {
        return get(level.dimension());
    }

    @Nullable
    public static Planet get(String identifier) {
        for (Planet planet : ALL_PLANETS)
            if (planet.IDENTIFIER.equals(identifier))
                return planet;

        return null;
    }
}
