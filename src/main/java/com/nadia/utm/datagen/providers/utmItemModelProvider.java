package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class utmItemModelProvider extends ItemModelProvider {
    public utmItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, "utm", existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(utmBlocks.GLINT_TABLE.item.getId().toString(), modLoc("block/glint_table"));
        withExistingParent(utmBlocks.HEAVY_METAL_ANVIL.item.getId().toString(), modLoc("block/heavy_metal_anvil"));
        withExistingParent(utmBlocks.GRATE.item.getId().toString(), modLoc("block/grate"));
        withExistingParent(utmBlocks.CHUNK_LOADER.item.getId().toString(), modLoc("block/chunk_loader"));
        withExistingParent(utmBlocks.PLAYER_CHUNK_LOADER.item.getId().toString(), modLoc("block/player_chunk_loader"));
        withExistingParent(utmBlocks.INTERDICTOR.item.getId().toString(), modLoc("block/interdictor"));

        basicItem(utmItems.UNFINISHED_GLINT_TABLE.get());

        basicItem(utmItems.INCREDI.get());
        basicItem(utmItems.FIDDLECORE.get());
        basicItem(utmItems.FLOATINGCORE.get());

        musicDisc(utmItems.MUSIC_DISC_TEARS.get());
        musicDisc(utmItems.MUSIC_DISC_LAVA_CHICKEN.get());

        basicItem(utmItems.ELYTRA_TRIM_VEIN.get());
        basicItem(utmItems.ELYTRA_TRIM_OUTWARD.get());
        basicItem(utmItems.ELYTRA_TRIM_LESSER.get());
        basicItem(utmItems.ELYTRA_TRIM_SPADES.get());
        basicItem(utmItems.ELYTRA_TRIM_HEARTSTWINGS.get());
        basicItem(utmItems.ELYTRA_TRIM_ROADRUNNER.get());
        basicItem(utmItems.ELYTRA_TRIM_ECOLOGIST.get());

        handheldItem(utmTools.COPPER_SWORD.get());
        handheldItem(utmTools.COPPER_PICKAXE.get());
        handheldItem(utmTools.COPPER_AXE.get());
        handheldItem(utmTools.COPPER_SHOVEL.get());
        handheldItem(utmTools.COPPER_HOE.get());

        handheldItem(utmTools.ENCHANTED_SWORD_BLUE.get());
        handheldItem(utmTools.ENCHANTED_SWORD_RED.get());
        handheldItem(utmTools.ENCHANTED_SWORD_GREEN.get());

        handheldItem(utmTools.OBSIDIAN_SWORD.get());
        basicItem(utmTools.NETHERYTRA.get());
    }

    public ItemModelBuilder musicDisc(ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/template_music_disc"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public ItemModelBuilder musicDisc(Item item) {
        return musicDisc(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }
}