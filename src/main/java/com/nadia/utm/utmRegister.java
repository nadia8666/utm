package com.nadia.utm;

import com.nadia.utm.block.HeavyMetalAnvilBlock;
import com.nadia.utm.block.utmBlockContainer;
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

public class utmRegister {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("utm");
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("utm");
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "utm");

    public static <B extends Block> DeferredBlock<B> register(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        return BLOCKS.registerBlock(name, func, props);
    }
    public static <B extends Block> utmBlockContainer<B, BlockItem> iregister(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        DeferredBlock<B> block = register(name, func, props);
        return new utmBlockContainer<B, BlockItem>(block, ITEMS.registerSimpleBlockItem(block));
    }

    public static final utmBlockContainer<HeavyMetalAnvilBlock, BlockItem> HEAVY_METAL_ANVIL = iregister("heavy_metal_anvil", HeavyMetalAnvilBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(7.0f)
            .explosionResistance(2500f).sound(SoundType.ANVIL));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS
            .register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.utm"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(Items.ANVIL::getDefaultInstance)
                    .displayItems((parameters, output) -> {
                        output.accept(HEAVY_METAL_ANVIL.block.get());
                    }).build());

    static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(HEAVY_METAL_ANVIL.item);
        }
    }
}
