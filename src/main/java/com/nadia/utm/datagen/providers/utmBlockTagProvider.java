package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class utmBlockTagProvider extends BlockTagsProvider {
    public utmBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "utm", existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.ANVIL).add(utmBlocks.HEAVY_METAL_ANVIL.block.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(utmBlocks.HEAVY_METAL_ANVIL.block.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(utmBlocks.HEAVY_METAL_ANVIL.block.get());
    }
}
