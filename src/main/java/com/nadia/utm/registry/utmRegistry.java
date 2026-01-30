package com.nadia.utm.registry;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class utmRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "utm");

    public static final DeferredRegister.Items ITEMS = utmItems.ITEMS;
    public static final DeferredRegister.Blocks BLOCKS = utmBlocks.BLOCKS;
    public static final DeferredRegister.DataComponents COMPONENTS = utmDataComponents.COMPONENTS;

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS
            .register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.utm"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(Items.ANVIL::getDefaultInstance)
                    .displayItems((parameters, output) -> {
                        utmRegistry.BLOCKS.getEntries().forEach(entry -> output.accept(entry.get()));
                        utmRegistry.ITEMS.getEntries().forEach(entry -> output.accept(entry.get()));
                    }).build());

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(utmBlocks.HEAVY_METAL_ANVIL.item);
        }
    }

    public static void registerAll(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        COMPONENTS.register(modEventBus);

        utmTools.doNothing();
        TABS.register(modEventBus);
    }
}
