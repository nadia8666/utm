package com.nadia.utm.registry.block;

import com.nadia.utm.block.*;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.registry.tags.utmTags;
import com.nadia.utm.registry.utmRegistry;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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

import static com.nadia.utm.client.ponder.utmPonderPlugin.A23;
import static com.nadia.utm.client.ponder.utmPonderPlugin.OXYGEN;
import static com.simibubi.create.infrastructure.ponder.AllCreatePonderTags.CONTRAPTION_ACTOR;

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

        return new utmBlockContainer<>(name, blockTarget, itemTarget, blockCallbacks);
    }

    public static final utmBlockContainer<HeavyMetalAnvilBlock, BlockItem> HEAVY_METAL_ANVIL = dualRegister("heavy_metal_anvil", HeavyMetalAnvilBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(7.0f)
            .explosionResistance(2500f).sound(SoundType.ANVIL)).drops().copyItemModel().minePick().mineTier(3).tags(BlockTags.ANVIL, ItemTags.ANVIL);

    public static final utmBlockContainer<GlintTableBlock, BlockItem> GLINT_TABLE = dualRegister("glint_table", GlintTableBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(3.0f)
            .explosionResistance(25f).sound(SoundType.WOOD)).drops().copyItemModel().mineAxe();

    public static final utmBlockContainer<LaunchContraptionBlock, BlockItem> LAUNCH_CONTRAPTION = dualRegister("launch_contraption", LaunchContraptionBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(3.0f)
            .sound(SoundType.METAL)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false))
            .drops().copyItemModel().ponder(A23, CONTRAPTION_ACTOR).minePick();

    public static final utmBlockContainer<OxygenCollectorBlock, BlockItem> OXYGEN_COLLECTOR = dualRegister("oxygen_collector", OxygenCollectorBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(3.0f)
            .sound(SoundType.METAL)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false))
            .stress(8).drops().ponder(A23, OXYGEN).minePick();

    public static final utmBlockContainer<OxygenFurnaceBlock, BlockItem> OXYGEN_FURNACE = dualRegister("oxygen_furnace", OxygenFurnaceBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(5.0f)
            .explosionResistance(50f).sound(SoundType.METAL)
            .noOcclusion())
            .drops().copyItemModel().ponder(A23, OXYGEN).minePick();

    public static final utmBlockContainer<PortasealerBlock, BlockItem> PORTASEALER = dualRegister("portasealer", PortasealerBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(2.0f)
            .explosionResistance(50f).sound(SoundType.METAL)
            .noOcclusion().isSuffocating((s, l, p) -> false)
            .isViewBlocking((s, l, p) -> false))
            .drops().copyItemModel().ponder(A23, OXYGEN).minePick();

    public static final utmBlockContainer<BiomeSealerBlock, BlockItem> BIOME_SEALER = dualRegister("biome_sealer", BiomeSealerBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(5.0f)
            .explosionResistance(50f).sound(SoundType.METAL))
            .drops().copyItemModel().ponder(A23, OXYGEN).minePick().stress(2);

    public static final utmBlockContainer<BlockChunkLoaderBlock, BlockItem> CHUNK_LOADER = dualRegister("chunk_loader", BlockChunkLoaderBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(5.0f)
            .explosionResistance(25f).sound(SoundType.WOOD)
            .noOcclusion()).drops().copyItemModel().mineAxe().mineTier(1);

    public static final utmBlockContainer<PlayerChunkLoaderBlock, BlockItem> PLAYER_CHUNK_LOADER = dualRegister("player_chunk_loader", PlayerChunkLoaderBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(5.0f)
            .explosionResistance(25f).sound(SoundType.WOOD)
            .noOcclusion()).drops().copyItemModel().mineAxe().mineTier(1);

    public static final utmBlockContainer<GrateBlock, BlockItem> GRATE = dualRegister("grate", GrateBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.CHAIN)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)).drops().copyItemModel().minePick();

    public static final utmBlockContainer<AerowallBlock, BlockItem> AERO_WALL = dualRegister("aerowall", AerowallBlock::new, BlockBehaviour.Properties.of()
            .instabreak()
            .sound(SoundType.SLIME_BLOCK)
            .noOcclusion()
            .noCollission()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)).drops().tags(utmTags.BLOCK.SEAL_NOPROP);

    public static final utmBlockContainer<CitywallsBlock, BlockItem> CITYWALLS_METAL = dualRegister("citywalls_metal", CitywallsBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(1.0f)
            .sound(SoundType.ANCIENT_DEBRIS)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)).drops(),
            OUTPOSTWALLS_METAL = dualRegister("outpostwalls_metal", CitywallsBlock::new, BlockBehaviour.Properties.of()
                    .destroyTime(1.0f)
                    .sound(SoundType.ANCIENT_DEBRIS)
                    .noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)).drops(),
            OUTPOSTWALLS_SHRINE = dualRegister("outpostwalls_shrine", CitywallsBlock::new, BlockBehaviour.Properties.of()
                    .destroyTime(1.0f)
                    .sound(SoundType.ANCIENT_DEBRIS)
                    .noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)).drops(),
            CITYWALLS_SHRINE = dualRegister("citywalls_shrine", CitywallsBlock::new, BlockBehaviour.Properties.of()
                    .destroyTime(1.0f)
                    .sound(SoundType.ANCIENT_DEBRIS)
                    .noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)).drops();
    public static final utmBlockContainer<InterdictorBlock, BlockItem> INTERDICTOR = dualRegister("interdictor", InterdictorBlock::new, BlockBehaviour.Properties.of()
            .destroyTime(10.0f)
            .sound(SoundType.NETHERITE_BLOCK)
            .noOcclusion()
            .isViewBlocking((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)).drops().copyItemModel();
    public static final utmBlockContainer<Block, BlockItem> FLINT_BLOCK = dualRegister("flint_block", Block::new, BlockBehaviour.Properties.of()
            .destroyTime(10.0f)
            .sound(SoundType.GRAVEL)).drops().copyItemModel().mineShovel();
    public static final utmBlockContainer<Block, BlockItem> FLINT_BLOCK_BLOCK = dualRegister("flint_block_block", Block::new, BlockBehaviour.Properties.of()
            .destroyTime(100.0f)
            .sound(SoundType.GRAVEL)).drops().copyItemModel().mineShovel();

    public static final DeferredBlock<LiquidBlock> LIQUID_OXYGEN_BLOCK = BLOCKS.register("liquid_oxygen",
            () -> new LiquidBlock(utmFluids.LIQUID_OXYGEN.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredBlock<LiquidBlock> MOLTEN_STEEL_BLOCK = BLOCKS.register("molten_steel",
            () -> new LiquidBlock(utmFluids.MOLTEN_STEEL.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));

    public static final utmBlockContainer<Block, BlockItem> ALUMINUM_ORE = dualRegister("aluminum_ore", Block::new, BlockBehaviour.Properties.of()
            .destroyTime(3)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)).dropOre(utmItems.RAW_ALUMINUM).bModel().copyItemModel().minePick().mineTier(2);

    public static final utmBlockContainer<Block, BlockItem> MAGNESIUM_ORE = dualRegister("magnesium_ore", Block::new, BlockBehaviour.Properties.of()
            .destroyTime(3)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)).dropOre(utmItems.RAW_MAGNESIUM).bModel().copyItemModel().minePick().mineTier(2);
}
