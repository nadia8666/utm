package com.nadia.utm.registry.block;

import com.mojang.datafixers.types.Type;
import com.nadia.utm.block.entity.ChunkLoaderBlockEntity;
import com.nadia.utm.block.entity.CitywallsBlockEntity;
import com.nadia.utm.block.entity.GlintTableBlockEntity;
import com.nadia.utm.block.entity.OxygenCollectorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class utmBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "utm");

    public static final Supplier<BlockEntityType<GlintTableBlockEntity>> GLINT_TABLE = BLOCK_ENTITIES.register("glint_table", () ->
            BlockEntityType.Builder.of(GlintTableBlockEntity::new, utmBlocks.GLINT_TABLE.block.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<ChunkLoaderBlockEntity>> CHUNK_LOADER = BLOCK_ENTITIES.register("chunk_loader", () ->
            BlockEntityType.Builder.of(ChunkLoaderBlockEntity::new, utmBlocks.CHUNK_LOADER.block.get(), utmBlocks.PLAYER_CHUNK_LOADER.block.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<CitywallsBlockEntity>> CITYWALLS_METAL = BLOCK_ENTITIES.register("citywalls_metal", () ->
            BlockEntityType.Builder.of(CitywallsBlockEntity::new, utmBlocks.CITYWALLS_METAL.block.get(), utmBlocks.OUTPOSTWALLS_METAL.block.get(),
                            utmBlocks.OUTPOSTWALLS_SHRINE.block.get(),utmBlocks.CITYWALLS_SHRINE.block.get())
                    .build((Type<?>) null));

    public static final Supplier<BlockEntityType<OxygenCollectorBlockEntity>> OXYGEN_COLLECTOR = BLOCK_ENTITIES.register("oxygen_collector", () ->
            BlockEntityType.Builder.of(OxygenCollectorBlockEntity::new, utmBlocks.OXYGEN_COLLECTOR.block.get())
                    .build((Type<?>) null));
}
