package com.nadia.utm.registry.block;

import com.nadia.utm.block.*;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.registry.utmRegistry;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class utmBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("utm");

    public static <B extends Block> DeferredBlock<B> register(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        return BLOCKS.registerBlock(name, func, props);
    }

    public static <B extends Block> utmBlockContainer<B, BlockItem> dualRegister(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        List<Consumer<? super B>> blockCallbacks = new ArrayList<>();
        List<BiConsumer<? super B, BlockItem>> pairCallbacks = new ArrayList<>();

        pairCallbacks.add((b, item) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                TooltipModifier modifier = new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE).andThen(TooltipModifier.mapNull(KineticStats.create(item)));
                TooltipModifier.REGISTRY.register(item, modifier);
            }
        });

        Function<BlockBehaviour.Properties, B> wrappedFunc = p -> {
            B b = func.apply(p);
            blockCallbacks.forEach(c -> c.accept(b));
            return b;
        };

        DeferredBlock<B> blockTarget = register(name, wrappedFunc, props);

        DeferredItem<BlockItem> itemTarget = utmRegistry.ITEMS.registerItem(name, p -> {
            BlockItem i = new BlockItem(blockTarget.get(), p);
            pairCallbacks.forEach(c -> c.accept(blockTarget.get(), i));
            return i;
        });

        return new utmBlockContainer<>(blockTarget, itemTarget, blockCallbacks);
    }

    public static final utmBlockContainer<HeavyMetalAnvilBlock, BlockItem> HEAVY_METAL_ANVIL = dualRegister("heavy_metal_anvil", HeavyMetalAnvilBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(7.0f)
            .explosionResistance(2500f).sound(SoundType.ANVIL));

    public static final utmBlockContainer<GlintTableBlock, BlockItem> GLINT_TABLE = dualRegister("glint_table", GlintTableBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(3.0f)
            .explosionResistance(25f).sound(SoundType.WOOD));

    public static final utmBlockContainer<LaunchContraptionBlock, BlockItem> LAUNCH_CONTRAPTION = dualRegister("launch_contraption", LaunchContraptionBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(3.0f)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false));

    public static final utmBlockContainer<OxygenCollectorBlock, BlockItem> OXYGEN_COLLECTOR = dualRegister("oxygen_collector", OxygenCollectorBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(3.0f)
            .sound(SoundType.METAL)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)).stress(8);

    public static final utmBlockContainer<BlockChunkLoaderBlock, BlockItem> CHUNK_LOADER = dualRegister("chunk_loader", BlockChunkLoaderBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(5.0f)
            .explosionResistance(25f).sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .noOcclusion());

    public static final utmBlockContainer<PlayerChunkLoaderBlock, BlockItem> PLAYER_CHUNK_LOADER = dualRegister("player_chunk_loader", PlayerChunkLoaderBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(5.0f)
            .explosionResistance(25f).sound(SoundType.WOOD)
            .requiresCorrectToolForDrops()
            .noOcclusion());

    public static final utmBlockContainer<GrateBlock, BlockItem> GRATE = dualRegister("grate", GrateBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.CHAIN)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false));

    public static final utmBlockContainer<CitywallsBlock, BlockItem> CITYWALLS_METAL = dualRegister("citywalls_metal", CitywallsBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.ANCIENT_DEBRIS)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false));
    public static final utmBlockContainer<CitywallsBlock, BlockItem> OUTPOSTWALLS_METAL = dualRegister("outpostwalls_metal", CitywallsBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.ANCIENT_DEBRIS)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false));
    public static final utmBlockContainer<CitywallsBlock, BlockItem> OUTPOSTWALLS_SHRINE = dualRegister("outpostwalls_shrine", CitywallsBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.ANCIENT_DEBRIS)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false));
    public static final utmBlockContainer<CitywallsBlock, BlockItem> CITYWALLS_SHRINE = dualRegister("citywalls_shrine", CitywallsBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.ANCIENT_DEBRIS)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false));
    public static final utmBlockContainer<InterdictorBlock, BlockItem> INTERDICTOR = dualRegister("interdictor", InterdictorBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(10.0f)
            .sound(SoundType.NETHERITE_BLOCK)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false));
    public static final utmBlockContainer<Block, BlockItem> FLINT_BLOCK = dualRegister("flint_block", Block::new, BlockBehaviour.Properties.of()
            .destroyTime(10.0f)
            .sound(SoundType.GRAVEL));
    public static final utmBlockContainer<Block, BlockItem> FLINT_BLOCK_BLOCK = dualRegister("flint_block_block", Block::new, BlockBehaviour.Properties.of()
            .destroyTime(100.0f)
            .sound(SoundType.GRAVEL));

    public static final DeferredBlock<LiquidBlock> LIQUID_OXYGEN_BLOCK = BLOCKS.register("liquid_oxygen",
            () -> new LiquidBlock(utmFluids.LIQUID_OXYGEN.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
}
