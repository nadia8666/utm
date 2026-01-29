package com.nadia.utm.registry;

import com.nadia.utm.block.HeavyMetalAnvilBlock;
import com.nadia.utm.block.utmBlockContainer;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class utmRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "utm");

    public static final DeferredRegister.Items ITEMS = utmItems.ITEMS;
    public static final DeferredRegister.Blocks BLOCKS = utmBlocks.BLOCKS;

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS
            .register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.utm"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(Items.ANVIL::getDefaultInstance)
                    .displayItems((parameters, output) -> {
                        output.accept(utmBlocks.HEAVY_METAL_ANVIL.block.get());

                        output.accept(utmTools.COPPER_SWORD.get());
                        output.accept(utmTools.COPPER_PICKAXE.get());
                        output.accept(utmTools.COPPER_AXE.get());
                        output.accept(utmTools.COPPER_SHOVEL.get());
                        output.accept(utmTools.COPPER_HOE.get());

                        output.accept(utmTools.AWESOME_AXE.get());
                    }).build());

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(utmBlocks.HEAVY_METAL_ANVIL.item);
        }
    }

    public static void registerAll() {
        utmTools.registerTools();
    }
}
