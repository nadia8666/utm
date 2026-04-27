package com.nadia.utm.datagen.providers;

import com.nadia.utm.behavior.space.SpaceStateHandler;
import com.nadia.utm.registry.block.utmBlockContainer;
import com.nadia.utm.registry.tags.utmTags;
import com.simibubi.create.AllBlocks;
import dev.ryanhcode.offroad.index.OffroadBlocks;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        tag(utmTags.BLOCK.A23_ORE_REPLACEABLE).add(SpaceStateHandler.UNMODIFIED_BLOCKS.toArray(Block[]::new));

        tag(utmTags.BLOCK.UNSEALED).add(AllBlocks.PORTABLE_FLUID_INTERFACE.get()
                , AllBlocks.PORTABLE_STORAGE_INTERFACE.get()
                , AllBlocks.WATER_WHEEL.get()
                , AllBlocks.LARGE_WATER_WHEEL.get()
                , AllBlocks.CRUSHING_WHEEL.get()
                , AllBlocks.CLOCKWORK_BEARING.get()
                , AllBlocks.MECHANICAL_BEARING.get()
                , AllBlocks.WINDMILL_BEARING.get()
                , AllBlocks.BELT.get()
                , AllBlocks.CHUTE.get()

                , Blocks.HOPPER

                , SimBlocks.REDSTONE_MAGNET.get()
                , SimBlocks.LASER_POINTER.get()
                , SimBlocks.LASER_SENSOR.get()
                , SimBlocks.SWIVEL_BEARING.get()
                , SimBlocks.ANALOG_TRANSMISSION.get()
                , SimBlocks.DOCKING_CONNECTOR.get()

                , OffroadBlocks.BOREHEAD_BEARING_BLOCK.get()
                , OffroadBlocks.WHEEL_MOUNT.get()
        );

        utmBlockContainer.ALL_BLOCKS.forEach(c -> {
            for (TagKey<Block> targ : c.DATAGEN_BLOCK_TAGS) {
                tag(targ).add(c.BLOCK.get());
            }
        });

        utmBlockContainer.DATAGEN_TARGETS.forEach((container, tags) -> {
            Block block = container.BLOCK.get();

            for (String tag : tags) {
                switch (tag) {
                    case "mine:pickaxe" -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                    case "mine:axe" -> tag(BlockTags.MINEABLE_WITH_AXE).add(block);
                    case "mine:shovel" -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block);
                    case "mine:hoe" -> tag(BlockTags.MINEABLE_WITH_HOE).add(block);
                }

                if (tag.startsWith("tier:")) {
                    String tier = tag.split(":")[1];
                    switch (tier) {
                        case "1" -> tag(BlockTags.NEEDS_STONE_TOOL).add(block);
                        case "2" -> tag(BlockTags.NEEDS_IRON_TOOL).add(block);
                        case "3" -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
                    }
                }
            }
        });
    }
}
