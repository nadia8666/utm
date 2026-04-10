package com.nadia.utm.util;

import com.nadia.utm.behavior.space.SealedChunkData;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OxyUtil {
    /**
     * @see #getCollectionStrength(Level, BlockPos, Integer, boolean)
     */
    public static double getCollectionStrength(Level level, BlockPos pos) {
        return getCollectionStrength(level, pos, null, false);
    }


    /**
     * Collects nearby leaves and returns a strength alpha accordingly
     * Always assumes 100% leaf coverage while in {@link #hasOxygen(Level) breathable} levels
     * @param level target level
     * @param pos target position
     * @param radius amount of blocks to check, default 3 = 7x7x7 area
     * @param raw modifies the output to return the raw amount of leaves nearby instead of
     * @return collection strength
     */
    public static double getCollectionStrength(Level level, BlockPos pos, @Nullable Integer radius, boolean raw) {
        double str = 0;
        int targetRadius = radius == null ? 3 : radius;
        int pow = (int) Math.pow(targetRadius * 2 + 1, 3);

        if (hasOxygen(level)) {
            str = pow;
        } else {
            for (BlockPos target : BlockPos.betweenClosed(
                    pos.offset(-targetRadius, -targetRadius, -targetRadius),
                    pos.offset(targetRadius, targetRadius, targetRadius)
            )) {
                BlockState block = level.getBlockState(target);
                if (block.is(BlockTags.LEAVES))
                    str++;
            }
        }

        if (!raw && str > 0) {
            if (str - 32 > 0)
                str = str / (pow - 1);
            else
                str = 0;
        }

        return str;
    }

    public static final Set<ResourceKey<Level>> UNBREATHABLE_DIMENSIONS = new HashSet<>();
    static {
        UNBREATHABLE_DIMENSIONS.add(utmDimensions.AG_KEY);
    }

    /**
     * Checks if a level has oxygen or not
     * @param level target level
     * @return if the level is breathable (has oxygen)
     *
     * @see #hasOxygen(Level)
     * @see #hasOxygen(ResourceKey)
     * @see #canBreathe(Entity)
     */
    public static boolean hasOxygen(Level level) {
        return !UNBREATHABLE_DIMENSIONS.contains(level.dimension());
    }

    /**
     * @see #hasOxygen(Level)
     * @see #hasOxygen(ResourceKey)
     * @see #canBreathe(Entity)
     */
    public static boolean hasOxygen(ResourceKey<Level> dimension) {
        return !UNBREATHABLE_DIMENSIONS.contains(dimension);
    }

    /**
     * @see #hasOxygen(Level)
     * @see #hasOxygen(ResourceKey)
     * @see #canBreathe(Entity)
     */
    public static boolean canBreathe(Entity entity) {
        return !UNBREATHABLE_DIMENSIONS.contains(entity.level().dimension());
    }

    /**
     * set sealed status of block pos
     * @param level target level
     * @param targetPos block pos
     * @param controllerPos nullable controller position
     */
    public static void setBlockSealed(ServerLevel level, BlockPos targetPos, @Nullable BlockPos controllerPos) {
        ChunkPos chunkPos = new ChunkPos(targetPos);
        LevelChunk chunk = (LevelChunk) level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
        if (chunk == null) return;

        SealedChunkData currentData = chunk.getData(utmAttachments.SEALED_AIR);
        Map<BlockPos, BlockPos> updatedMap = new HashMap<>(currentData.sealedBlocks());

        if (controllerPos != null)
            updatedMap.put(targetPos, controllerPos);
        else
            updatedMap.remove(targetPos);

        chunk.setData(utmAttachments.SEALED_AIR, new SealedChunkData(updatedMap));
        chunk.setUnsaved(true);
    }

    /**
     * check if target block is sealed
     * @param level target level to check
     * @param targetPos block pos
     * @return controller position
     */
    @Nullable
    public static BlockPos isSealed(ServerLevel level, BlockPos targetPos) {
        ChunkPos chunkPos = new ChunkPos(targetPos);
        LevelChunk chunk = (LevelChunk) level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
        if (chunk == null) return null;

        return chunk.getData(utmAttachments.SEALED_AIR).sealedBlocks().get(targetPos);
    }
}