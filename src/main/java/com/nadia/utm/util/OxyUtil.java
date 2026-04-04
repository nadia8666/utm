package com.nadia.utm.util;

import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class OxyUtil {
    public static double getCollectionStrength(Level level, BlockPos pos) {
        return getCollectionStrength(level, pos, null, false);
    }
    public static double getCollectionStrength(Level level, BlockPos pos, @Nullable Integer radius, boolean raw) {
        double str = 0;
        int targetRadius = radius == null ? 3 : radius;

        for (BlockPos target : BlockPos.betweenClosed(
                pos.offset(-targetRadius, -targetRadius, -targetRadius),
                pos.offset(targetRadius, targetRadius, targetRadius)
        )) {
                    BlockState block = level.getBlockState(target);
                    if (block.is(BlockTags.LEAVES))
                        str++;
        }

        if (!raw && str > 0) {
            str = Math.max(0, str - 32);

            if (str > 0)
                str = str / 312;
        }

        return str;
    }

    private static final Set<ResourceKey<Level>> UNBREATHABLE_DIMENSIONS = new HashSet<>();
    static {
        UNBREATHABLE_DIMENSIONS.add(utmDimensions.AG_KEY);
    }

    public static boolean hasOxygen(Level level) {
        return !UNBREATHABLE_DIMENSIONS.contains(level.dimension());
    }

    public static boolean hasOxygen(ResourceKey<Level> dimension) {
        return !UNBREATHABLE_DIMENSIONS.contains(dimension);
    }

    public static boolean canBreathe(Entity entity) {
        return !UNBREATHABLE_DIMENSIONS.contains(entity.level().dimension());
    }
}