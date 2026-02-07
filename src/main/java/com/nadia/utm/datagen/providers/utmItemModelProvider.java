package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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

        basicItem(utmItems.UNFINISHED_GLINT_TABLE.get());

        handheldItem(utmTools.COPPER_SWORD.get());
        handheldItem(utmTools.COPPER_PICKAXE.get());
        handheldItem(utmTools.COPPER_AXE.get());
        handheldItem(utmTools.COPPER_SHOVEL.get());
        handheldItem(utmTools.COPPER_HOE.get());

        handheldItem(utmTools.OBSIDIAN_SWORD.get());
        basicItem(utmTools.NETHERYTRA.get());
    }
}