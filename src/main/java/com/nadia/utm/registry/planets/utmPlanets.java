package com.nadia.utm.registry.planets;

import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class utmPlanets {
    public static final Set<Planet> ALL_PLANETS = new HashSet<>();
    public static final Map<ResourceKey<Level>, Planet> KEY_SET = new HashMap<>();

    public record Planet(ResourceKey<Level> KEY, String IDENTIFIER, double GRAVITY) {
            public Planet(ResourceKey<Level> KEY, String IDENTIFIER, double GRAVITY) {
                this.KEY = KEY;
                this.IDENTIFIER = IDENTIFIER;
                this.GRAVITY = GRAVITY;

                ALL_PLANETS.add(this);
                KEY_SET.put(KEY, this);
            }

            public boolean is(@Nullable Planet target) {
                return this.equals(target);
            }
        }

    public static final Planet EARTH = new Planet(Level.OVERWORLD, "earth", 0.08);
    public static Planet SPACE = new Planet(utmDimensions.SPACE_KEY, "space", 0);
    public static final Planet AG23 = new Planet(utmDimensions.AG_KEY, "2313ag", 0.12);

    @Nullable
    public static Planet get(ResourceKey<Level> key) {
        return KEY_SET.get(key);
    }

    @Nullable
    public static Planet get(Level level) {
        return get(level.dimension());
    }

    @Nullable
    public static Planet get(Entity entity) {
        return get(entity.level().dimension());
    }

    @Nullable
    public static Planet get(String identifier) {
        for (Planet planet : ALL_PLANETS)
            if (planet.IDENTIFIER.equals(identifier))
                return planet;

        return null;
    }
}
