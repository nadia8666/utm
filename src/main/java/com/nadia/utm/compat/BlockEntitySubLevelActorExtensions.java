package com.nadia.utm.compat;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public interface BlockEntitySubLevelActorExtensions<T extends BlockEntity & BlockEntitySubLevelActor> extends BlockEntitySubLevelActor {
    /**
     * Called on all sublevel actors whenever sublevels change dimensions
     *
     * @param conversions sublevel conversion list for translating UUIDs
     * @param oldBE       blockentity in the previous sublevel
     * @param transforms  sublevel assembly transform for translating positions
     * @return if the data changed
     */
    default boolean sable$migrateData(Map<ServerSubLevel, ServerSubLevel> conversions, T oldBE, Map<ServerSubLevel, SubLevelAssemblyHelper.AssemblyTransform> transforms) {
        return false;
    }

    /**
     * Called to clean NBT data when loading actor blockentities, use to remove no longer accurate tags when changing dimension
     */
    default void sable$cleanLevelNBT(CompoundTag tag) {
    }
}
