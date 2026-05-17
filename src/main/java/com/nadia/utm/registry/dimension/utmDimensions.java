package com.nadia.utm.registry.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class utmDimensions {
    public static final ResourceKey<Level> AG_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("utm", "2313ag")
    );

    public static final ResourceKey<DimensionType> AG_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath("utm", "2313ag_type")
    );

    public static final ResourceKey<Level> SPACE_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("utm", "space")
    );

    public static final ResourceKey<DimensionType> SPACE_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath("utm", "space_type")
    );

    public static final ResourceKey<Level> MOON_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("utm", "moon")
    );

    public static final ResourceKey<DimensionType> MOON_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath("utm", "moon_type")
    );

    public static final ResourceKey<Level> SUN_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("utm", "sun")
    );

    public static final ResourceKey<DimensionType> SUN_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath("utm", "sun_type")
    );
}
