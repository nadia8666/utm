package com.nadia.utm.registry.block;

import com.mojang.datafixers.types.Type;
import com.nadia.utm.block.entity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("RedundantCast")
public class utmBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "utm");

    public static final Supplier<BlockEntityType<GlintTableBlockEntity>> GLINT_TABLE = BLOCK_ENTITIES.register("glint_table", () ->
            BlockEntityType.Builder.of(GlintTableBlockEntity::new, utmBlocks.GLINT_TABLE.BLOCK.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<ChunkLoaderBlockEntity>> CHUNK_LOADER = BLOCK_ENTITIES.register("chunk_loader", () ->
            BlockEntityType.Builder.of(ChunkLoaderBlockEntity::new, utmBlocks.CHUNK_LOADER.BLOCK.get(), utmBlocks.PLAYER_CHUNK_LOADER.BLOCK.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<CitywallsBlockEntity>> CITYWALLS_METAL = BLOCK_ENTITIES.register("citywalls_metal", () ->
            BlockEntityType.Builder.of(CitywallsBlockEntity::new, utmBlocks.CITYWALLS_METAL.BLOCK.get(), utmBlocks.OUTPOSTWALLS_METAL.BLOCK.get(),
                            utmBlocks.OUTPOSTWALLS_SHRINE.BLOCK.get(),utmBlocks.CITYWALLS_SHRINE.BLOCK.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<OxygenCollectorBlockEntity>> OXYGEN_COLLECTOR = BLOCK_ENTITIES.register("oxygen_collector", () ->
            BlockEntityType.Builder.of(OxygenCollectorBlockEntity::new, utmBlocks.OXYGEN_COLLECTOR.BLOCK.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<OxygenFurnaceBlockEntity>> OXYGEN_FURNACE = BLOCK_ENTITIES.register("oxygen_furnace", () ->
            BlockEntityType.Builder.of(OxygenFurnaceBlockEntity::new, utmBlocks.OXYGEN_FURNACE.BLOCK.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<PortasealerBlockEntity>> PORTASEALER = BLOCK_ENTITIES.register("portasealer", () ->
            BlockEntityType.Builder.of(PortasealerBlockEntity::new, utmBlocks.PORTASEALER.BLOCK.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<BiomeSealerBlockEntity>> BIOME_SEALER = BLOCK_ENTITIES.register("biome_sealer", () ->
            BlockEntityType.Builder.of(BiomeSealerBlockEntity::new, utmBlocks.BIOME_SEALER.BLOCK.get())
                    .build((Type<?>) null));
}
