package com.nadia.utm.registry.block;

import com.mojang.datafixers.types.Type;
import com.nadia.utm.block.entity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("RedundantCast")
public class utmBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "utm");

    public static final Map<utmBlockContainer<?, ?>, BlockEntityType<?>> BLOCK_BINDINGS = new HashMap<>();

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, utmBlockContainer<?, ?>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            BlockEntityType<T> type = BlockEntityType.Builder.of(factory, Arrays.stream(blocks).map(c -> c.BLOCK.get()).toArray(Block[]::new)).build((Type<?>) null);

            for (utmBlockContainer<?, ?> block : blocks)
                BLOCK_BINDINGS.put(block, type);

            return type;
        });
    }

    public static final Supplier<BlockEntityType<GlintTableBlockEntity>> GLINT_TABLE = register("glint_table", GlintTableBlockEntity::new, utmBlocks.GLINT_TABLE);

    public static final Supplier<BlockEntityType<ChunkLoaderBlockEntity>> CHUNK_LOADER = register("chunk_loader", ChunkLoaderBlockEntity::new, utmBlocks.CHUNK_LOADER, utmBlocks.PLAYER_CHUNK_LOADER);

    public static final Supplier<BlockEntityType<CitywallsBlockEntity>> CITYWALLS_METAL = register("citywalls_metal", CitywallsBlockEntity::new,
            utmBlocks.CITYWALLS_METAL, utmBlocks.OUTPOSTWALLS_METAL, utmBlocks.OUTPOSTWALLS_SHRINE, utmBlocks.CITYWALLS_SHRINE);

    public static final Supplier<BlockEntityType<OxygenCollectorBlockEntity>> OXYGEN_COLLECTOR = register("oxygen_collector", OxygenCollectorBlockEntity::new, utmBlocks.OXYGEN_COLLECTOR);

    public static final Supplier<BlockEntityType<OxygenFurnaceBlockEntity>> OXYGEN_FURNACE = register("oxygen_furnace", OxygenFurnaceBlockEntity::new, utmBlocks.OXYGEN_FURNACE);

    public static final Supplier<BlockEntityType<PortasealerBlockEntity>> PORTASEALER = register("portasealer", PortasealerBlockEntity::new, utmBlocks.PORTASEALER);

    public static final Supplier<BlockEntityType<BiomeSealerBlockEntity>> BIOME_SEALER = register("biome_sealer", BiomeSealerBlockEntity::new, utmBlocks.BIOME_SEALER);

    public static final Supplier<BlockEntityType<IonJetBlockEntity>> ION_JET = register("ion_jet", IonJetBlockEntity::new, utmBlocks.ION_JET);

    public static final Supplier<BlockEntityType<AerowallBlockEntity>> AEROWALL = register("aerowall", AerowallBlockEntity::new, utmBlocks.AERO_WALL);
}
