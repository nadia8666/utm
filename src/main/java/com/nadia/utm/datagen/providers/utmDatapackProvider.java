package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.worldgen.utmFeatures;
import com.nadia.utm.utm;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class utmDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, utmFeatures.CONFIGURED::bootstrap)
            .add(Registries.PLACED_FEATURE, utmFeatures.PLACED::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, utmFeatures.BIOME::bootstrap);

    public utmDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(utm.MODID));
    }
}
