package com.nadia.utm.registry.block;

import com.nadia.utm.block.GlintTableBlock;
import com.nadia.utm.block.GrateBlock;
import com.nadia.utm.block.HeavyMetalAnvilBlock;
import com.nadia.utm.block.utmBlockContainer;
import com.nadia.utm.registry.utmRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class utmBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("utm");

    public static <B extends Block> DeferredBlock<B> register(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        return BLOCKS.registerBlock(name, func, props);
    }

    public static <B extends Block> utmBlockContainer<B, BlockItem> iregister(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        DeferredBlock<B> block = register(name, func, props);
        return new utmBlockContainer<>(block, utmRegistry.ITEMS.registerSimpleBlockItem(block));
    }

    public static final utmBlockContainer<HeavyMetalAnvilBlock, BlockItem> HEAVY_METAL_ANVIL = iregister("heavy_metal_anvil", HeavyMetalAnvilBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(7.0f)
            .explosionResistance(2500f).sound(SoundType.ANVIL));

    public static final utmBlockContainer<GlintTableBlock, BlockItem> GLINT_TABLE = iregister("glint_table", GlintTableBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(3.0f)
            .explosionResistance(25f).sound(SoundType.WOOD));

    public static final utmBlockContainer<GrateBlock, BlockItem> GRATE = iregister("grate", GrateBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.CHAIN)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)); //id like to make it waterlog but i dont knwo how!
}
