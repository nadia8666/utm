package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlockContainer;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.registry.utmRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

public class utmBlockLootTableProvider extends BlockLootSubProvider {
    public utmBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(utmBlocks.HEAVY_METAL_ANVIL.BLOCK.get());
        dropSelf(utmBlocks.GLINT_TABLE.BLOCK.get());
        dropSelf(utmBlocks.GRATE.BLOCK.get());
        dropSelf(utmBlocks.CITYWALLS_METAL.BLOCK.get());
        dropSelf(utmBlocks.CITYWALLS_SHRINE.BLOCK.get());
        dropSelf(utmBlocks.OUTPOSTWALLS_METAL.BLOCK.get());
        dropSelf(utmBlocks.OUTPOSTWALLS_SHRINE.BLOCK.get());
        dropSelf(utmBlocks.CHUNK_LOADER.BLOCK.get());
        dropSelf(utmBlocks.PLAYER_CHUNK_LOADER.BLOCK.get());
        dropSelf(utmBlocks.INTERDICTOR.BLOCK.get());
        dropSelf(utmBlocks.FLINT_BLOCK.BLOCK.get());
        dropSelf(utmBlocks.FLINT_BLOCK_BLOCK.BLOCK.get());
        dropSelf(utmBlocks.LAUNCH_CONTRAPTION.BLOCK.get());
        dropSelf(utmBlocks.OXYGEN_COLLECTOR.BLOCK.get());

        utmBlockContainer.DATAGEN_TAGS.forEach((c, tags) -> {
            for (String tag : tags) {
                if (tag.equals("dropSelf"))
                    dropSelf(c.BLOCK.get());
                if (tag.startsWith("dropOre:")) {
                    try {
                        add(c.BLOCK.get(), createOreDrop(c.BLOCK.get(), utmItems.fromName(Arrays.stream(tag.split(":")).toList().getLast())));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return utmRegistry.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
