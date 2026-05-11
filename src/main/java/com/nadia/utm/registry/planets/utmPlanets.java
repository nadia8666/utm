package com.nadia.utm.registry.planets;

import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class utmPlanets {
    public static final Set<Planet> ALL_PLANETS = new HashSet<>();
    public static final Map<ResourceKey<Level>, Planet> KEY_SET = new HashMap<>();

    public record Planet(ResourceKey<Level> KEY, String IDENTIFIER, double GRAVITY, Orbit ORBIT) {
        public Planet(ResourceKey<Level> KEY, String IDENTIFIER, double GRAVITY, Orbit ORBIT) {
            this.KEY = KEY;
            this.IDENTIFIER = IDENTIFIER;
            this.GRAVITY = GRAVITY;
            this.ORBIT = ORBIT;

            ALL_PLANETS.add(this);
            KEY_SET.put(KEY, this);
        }

        public boolean is(@Nullable Planet target) {
            return this.equals(target);
        }
    }

    public static Planet SPACE = new Planet(utmDimensions.SPACE_KEY, "space", 0, new Orbit.Static(new Vector3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY), 0));
    public static Planet SUN = new Planet(utmDimensions.SUN_KEY, "sun", 0, new Orbit.Static(new Vector3d(), Double.POSITIVE_INFINITY));
    public static final Planet EARTH = new Planet(Level.OVERWORLD, "earth", 0.08, new Orbit.Circular(
            SUN.ORBIT(),
            120000.0,
            0.00005,
            new Quaternionf(),
            30000
    ));
    public static Planet MOON = new Planet(utmDimensions.MOON_KEY, "moon", 0, new Orbit.Circular(
            EARTH.ORBIT(),
            4000.0,
            0.002,
            new Quaternionf().rotationXYZ(0.2f, 0, 0.1f),
            1500
    ));
    public static final Planet AG23 = new Planet(utmDimensions.AG_KEY, "2313ag", 0.12, new Orbit.Circular(
            EARTH.ORBIT(),
            9000.0,
            (2.0 * Math.PI) / 24000.0,
            new Quaternionf(),
            2500
    ));

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
