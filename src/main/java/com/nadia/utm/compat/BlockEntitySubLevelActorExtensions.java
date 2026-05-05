package com.nadia.utm.compat;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public interface BlockEntitySubLevelActorExtensions<T extends BlockEntitySubLevelActor> extends BlockEntitySubLevelActor {
    /**
     * Called on all sublevel actors whenever sublevels change dimensions
     *
     * @param conversions sublevel conversion list for translating UUIDs
     * @param oldBE       blockentity in the previous sublevel
     * @param transforms  sublevel assembly transform for translating positions
     * @return if the data changed
     */
    @CanIgnoreReturnValue
    default boolean sable$migrateData(final Map<ServerSubLevel, ServerSubLevel> conversions, final T oldBE, final Map<ServerSubLevel, SubLevelAssemblyHelper.AssemblyTransform> transforms) {
        return false;
    }

    /**
     * Called to clean NBT data when loading actor blockentities, use to remove no longer accurate tags when changing dimension
     */
    default void sable$cleanLevelNBT(final CompoundTag tag) {
    }
}
