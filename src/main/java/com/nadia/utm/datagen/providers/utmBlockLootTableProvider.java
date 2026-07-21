package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlockContainer;
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
        utmBlockContainer.DATAGEN_TARGETS.forEach((c, tags) -> {
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
        return utmRegistry.BLOCKS.getEntries().stream().filter(P -> utmBlockContainer.fromBlockUnsafe(P.get()).map(utmBlockContainer -> !utmBlockContainer.getForDatagen().contains("dropNull")).orElse(true)).map(Holder::value)::iterator;
    }
}
