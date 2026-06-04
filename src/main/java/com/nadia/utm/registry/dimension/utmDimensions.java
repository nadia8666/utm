package com.nadia.utm.registry.dimension;

import com.nadia.utm.utm;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class utmDimensions {
    public static final ResourceKey<Level> AG_KEY = ResourceKey.create(
            Registries.DIMENSION,
            utm.key("2313ag")
    );

    public static final ResourceKey<DimensionType> AG_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            utm.key("2313ag_type")
    );

    public static final ResourceKey<Level> SPACE_KEY = ResourceKey.create(
            Registries.DIMENSION,
            utm.key("space")
    );

    public static final ResourceKey<DimensionType> SPACE_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            utm.key("space_type")
    );

    public static final ResourceKey<Level> MOON_KEY = ResourceKey.create(
            Registries.DIMENSION,
            utm.key("moon")
    );

    public static final ResourceKey<DimensionType> MOON_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            utm.key("moon_type")
    );

    public static final ResourceKey<Level> SUN_KEY = ResourceKey.create(
            Registries.DIMENSION,
            utm.key("sun")
    );

    public static final ResourceKey<DimensionType> SUN_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            utm.key("sun_type")
    );

    public static final ResourceKey<Level> INTERSECTION_KEY = ResourceKey.create(
            Registries.DIMENSION,
            utm.key("intersection")
    );

    public static final ResourceKey<DimensionType> INTERSECTION_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            utm.key("intersection_type")
    );
}
