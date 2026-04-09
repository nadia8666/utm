package com.nadia.utm.registry.worldgen;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.tags.utmTags;
import com.nadia.utm.utm;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class utmFeatures {
    public static class CONFIGURED {
        public static final ResourceKey<ConfiguredFeature<?, ?>> ALUMINUM_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, utm.key("aluminum_ore"));
        public static final ResourceKey<ConfiguredFeature<?, ?>> MAGNESIUM_ORE = ResourceKey.create(Registries.CONFIGURED_FEATURE, utm.key("magnesium_ore"));

        public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
            RuleTest toReplace = new TagMatchTest(utmTags.BLOCK.A23_ORE_REPLACEABLE);

            context.register(ALUMINUM_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
                    OreConfiguration.target(toReplace, utmBlocks.ALUMINUM_ORE.BLOCK.get().defaultBlockState())
            ), 8)));

            context.register(MAGNESIUM_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
                    OreConfiguration.target(toReplace, utmBlocks.MAGNESIUM_ORE.BLOCK.get().defaultBlockState())
            ), 6)));
        }
    }

    public static class PLACED {
        public static final ResourceKey<PlacedFeature> ALUMINUM_ORE = ResourceKey.create(Registries.PLACED_FEATURE, utm.key("aluminum_ore_placed"));
        public static final ResourceKey<PlacedFeature> MAGNESIUM_ORE = ResourceKey.create(Registries.PLACED_FEATURE, utm.key("magnesium_ore_placed"));

        public static void bootstrap(BootstrapContext<PlacedFeature> context) {
            HolderGetter<ConfiguredFeature<?, ?>> feats = context.lookup(Registries.CONFIGURED_FEATURE);

            context.register(ALUMINUM_ORE, new PlacedFeature(feats.getOrThrow(CONFIGURED.ALUMINUM_ORE),
                    List.of(CountPlacement.of(6), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(319)), BiomeFilter.biome())));

            context.register(MAGNESIUM_ORE, new PlacedFeature(feats.getOrThrow(CONFIGURED.MAGNESIUM_ORE),
                    List.of(CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(319)), BiomeFilter.biome())));
        }
    }

    public static class BIOME {
        public static final ResourceKey<BiomeModifier> ORES = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, utm.key("2313ag_ores"));
        public static final ResourceKey<Biome> A23_CLIFFS = ResourceKey.create(Registries.BIOME, utm.key("2313ag_cliffs"));

        public static void bootstrap(BootstrapContext<BiomeModifier> context) {
            HolderGetter<PlacedFeature> feats = context.lookup(Registries.PLACED_FEATURE);
            HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

            context.register(ORES, new BiomeModifiers.AddFeaturesBiomeModifier(
                    HolderSet.direct(biomes.getOrThrow(A23_CLIFFS)),
                    HolderSet.direct(feats.getOrThrow(PLACED.ALUMINUM_ORE), feats.getOrThrow(PLACED.MAGNESIUM_ORE)),
                    GenerationStep.Decoration.UNDERGROUND_ORES
            ));
        }
    }
}
