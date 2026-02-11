package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.registry.tags.utmTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
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
        tag(ItemTags.ANVIL).add(utmBlocks.HEAVY_METAL_ANVIL.item.get());

        tag(Tags.Items.ENCHANTABLES).add(utmTools.OBSIDIAN_SWORD.get());
        tag(ItemTags.SWORD_ENCHANTABLE).add(utmTools.OBSIDIAN_SWORD.get());
        tag(ItemTags.WEAPON_ENCHANTABLE).add(utmTools.OBSIDIAN_SWORD.get()); //need both

        tag(utmTags.ITEMS.ELYTRA_TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_VEIN.get());
        tag(utmTags.ITEMS.ELYTRA_TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_OUTWARD.get());
        tag(utmTags.ITEMS.ELYTRA_TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_LESSER.get());
        tag(utmTags.ITEMS.ELYTRA_TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_HEARTSTWINGS.get());
        tag(utmTags.ITEMS.ELYTRA_TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_SPADES.get());
        tag(utmTags.ITEMS.ELYTRA_TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_ROADRUNNER.get());
        tag(utmTags.ITEMS.ELYTRA_TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_ECOLOGIST.get());

        tag(ItemTags.TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_VEIN.get());
        tag(ItemTags.TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_OUTWARD.get());
        tag(ItemTags.TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_LESSER.get());
        tag(ItemTags.TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_HEARTSTWINGS.get());
        tag(ItemTags.TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_SPADES.get());
        tag(ItemTags.TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_ROADRUNNER.get());
        tag(ItemTags.TRIM_TEMPLATES).add(utmItems.ELYTRA_TRIM_ECOLOGIST.get());
    }
}
