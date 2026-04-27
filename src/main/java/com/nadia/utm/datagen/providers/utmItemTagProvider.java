package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlockContainer;
import com.nadia.utm.registry.item.utmItemContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class utmItemTagProvider extends ItemTagsProvider {
    public utmItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, "utm", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        utmBlockContainer.ALL_BLOCKS.forEach(c -> {
            for (TagKey<Item> targ : c.DATAGEN_ITEM_TAGS) {
                tag(targ).add(c.ITEM.get());
            }
        });

        utmItemContainer.ALL_ITEMS.forEach(c -> {
            for (TagKey<Item> targ : c.DATAGEN_TAGS) {
                tag(targ).add(c.ITEM.get());
            }
        });
    }
}
