package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.utmRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class utmBlockLootTableProvider extends BlockLootSubProvider {
    public utmBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(utmBlocks.HEAVY_METAL_ANVIL.block.get());
        dropSelf(utmBlocks.GLINT_TABLE.block.get());
        dropSelf(utmBlocks.GRATE.block.get());
        dropSelf(utmBlocks.CITYWALLS_METAL.block.get());
        dropSelf(utmBlocks.CITYWALLS_SHRINE.block.get());
        dropSelf(utmBlocks.OUTPOSTWALLS_METAL.block.get());
        dropSelf(utmBlocks.OUTPOSTWALLS_SHRINE.block.get());
        dropSelf(utmBlocks.CHUNK_LOADER.block.get());
        dropSelf(utmBlocks.PLAYER_CHUNK_LOADER.block.get());
        dropSelf(utmBlocks.INTERDICTOR.block.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return utmRegistry.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
