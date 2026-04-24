package com.nadia.utm.util;

import com.nadia.utm.behavior.space.SealedChunkData;
import com.nadia.utm.mixin.BacktankUtilAccessor;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.registry.dimension.utmDimensions;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static com.simibubi.create.content.equipment.armor.BacktankUtil.getAir;

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
     *
     * @param level  target level
     * @param pos    target position
     * @param radius amount of blocks to check, default 3 = 7x7x7 area
     * @param raw    modifies the output to return the raw amount of leaves nearby instead of
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
     *
     * @param level target level
     * @return if the level is breathable (has oxygen)
     * @see #hasOxygen(Level)
     * @see #hasOxygen(ResourceKey)
     * @see #canBreathe(Entity)
     * @see #canBreatheFromSealed(ServerPlayer)
     * @see #isSealed(ServerLevel, BlockPos)
     */
    public static boolean hasOxygen(Level level) {
        return !UNBREATHABLE_DIMENSIONS.contains(level.dimension());
    }

    /**
     * @see #hasOxygen(Level)
     * @see #hasOxygen(ResourceKey)
     * @see #canBreathe(Entity)
     * @see #canBreatheFromSealed(ServerPlayer)
     * @see #isSealed(ServerLevel, BlockPos)
     */
    public static boolean hasOxygen(ResourceKey<Level> dimension) {
        return !UNBREATHABLE_DIMENSIONS.contains(dimension);
    }

    /**
     * @see #hasOxygen(Level)
     * @see #hasOxygen(ResourceKey)
     * @see #canBreathe(Entity)
     * @see #canBreatheFromSealed(ServerPlayer)
     * @see #isSealed(ServerLevel, BlockPos)
     */
    public static boolean canBreathe(Entity entity) {
        return !UNBREATHABLE_DIMENSIONS.contains(entity.level().dimension());
    }

    /**
     * @see #hasOxygen(Level)
     * @see #hasOxygen(ResourceKey)
     * @see #canBreathe(Entity)
     * @see #canBreatheFromSealed(ServerPlayer)
     * @see #isSealed(ServerLevel, BlockPos)
     */
    public static boolean canBreatheFromSealed(ServerPlayer player) {
        return isSealed(player.serverLevel(), player.blockPosition()) != null;
    }

    /**
     * set sealed status of block pos
     *
     * @param level         target level
     * @param targetPos     block pos
     * @param controllerPos nullable controller position
     */
    public static void setBlockSealed(ServerLevel level, BlockPos targetPos, @Nullable BlockPos controllerPos) {
        ChunkPos chunkPos = new ChunkPos(targetPos);
        ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
        if (chunk == null) return;

        SealedChunkData currentData = chunk.getData(utmAttachments.SEALED_AIR);
        Map<BlockPos, BlockPos> updatedMap = new HashMap<>(currentData.sealedBlocks());

        if (controllerPos != null)
            updatedMap.put(targetPos, controllerPos);
        else
            updatedMap.remove(targetPos);

        chunk.setData(utmAttachments.SEALED_AIR, new SealedChunkData(updatedMap));
    }

    /**
     * set sealed status of block pos in sable
     *
     * @param level         target sub level
     * @param targetPos     sable-local block pos
     * @param controllerPos nullable controller position
     */
    public static void setBlockSealed(SubLevel level, BlockPos targetPos, @Nullable BlockPos controllerPos) {
        LevelChunk chunk = SableUtil.getChunkLocalPos(level, targetPos);
        if (chunk == null) return;

        SealedChunkData currentData = chunk.getData(utmAttachments.SEALED_AIR);
        Map<BlockPos, BlockPos> updatedMap = new HashMap<>(currentData.sealedBlocks());

        if (controllerPos != null)
            updatedMap.put(targetPos, controllerPos);
        else
            updatedMap.remove(targetPos);
        chunk.setData(utmAttachments.SEALED_AIR, new SealedChunkData(updatedMap));
    }

    /**
     * check if target block is sealed
     *
     * @param level     target level to check
     * @param targetPos block pos
     * @return controller position
     */
    @Nullable
    public static BlockPos isSealed(ServerLevel level, BlockPos targetPos) {
        ChunkPos chunkPos = new ChunkPos(targetPos);
        ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY, false);
        if (chunk == null) return null;

        return chunk.getData(utmAttachments.SEALED_AIR).get(targetPos);
    }

    /**
     * @see #isSealed(ServerLevel, BlockPos)
     */
    @Nullable
    public static BlockPos isSealed(ServerLevel level, BlockPos... posList) {
        Map<BlockPos, BlockPos> hash = new HashMap<>();
        Set<ChunkPos> chunks = new HashSet<>();

        for (BlockPos pos : posList) {
            chunks.add(new ChunkPos(pos));
        }

        for (ChunkPos pos : chunks) {
            ChunkAccess chunk = level.getChunk(pos.x, pos.z, ChunkStatus.EMPTY, false);
            if (chunk != null)
                hash.putAll(chunk.getData(utmAttachments.SEALED_AIR).sealedBlocks());
        }

        for (BlockPos p : posList) {
            BlockPos controller = hash.get(p);
            if (controller != null) return controller;
        }

        return null;
    }

    /**
     * check if target block is sealed via sable
     *
     * @param level     target sublevel to check
     * @param posList various block positions
     * @return controller position
     */
    @Nullable
    public static BlockPos isSealed(SubLevel level, BlockPos... posList) {
        Map<BlockPos, BlockPos> hash = new HashMap<>();
        level.getPlot().getLoadedChunks().forEach(c -> hash.putAll(c.getChunk().getData(utmAttachments.SEALED_AIR).sealedBlocks()));

        for (BlockPos p : posList) {
            BlockPos controller = hash.get(SableUtil.toSublevelPos(level.logicalPose(), p));
            if (controller != null) return controller;
        }

        return null;
    }

    /**
     * get all backtanks an entity is wearing
     *
     * @param entity target entity
     * @return list of tank stacks
     */
    public static List<ItemStack> getAllBacktanks(LivingEntity entity) {
        List<ItemStack> all = new ArrayList<>();

        for (Function<LivingEntity, List<ItemStack>> supplier : BacktankUtilAccessor.getSuppliers()) {
            List<ItemStack> result = supplier.apply(entity);
            all.addAll(result);
        }

        all.sort((a, b) -> Float.compare(getAir(a), getAir(b)));

        return all;
    }

    /**
     * give a player temporary breathability in space
     * @param player target player
     * @param duration duration in ticks
     */
    public static void giveTemporaryAir(ServerPlayer player, int duration) {
        player.setData(utmAttachments.TEMPORARY_OXYGEN, duration);
    }

    public static List<BlockPos> getSealCheckPositions(Player player) {
        return BlockPos.betweenClosedStream(player.getBoundingBox().inflate(1)).map(BlockPos::immutable).toList();
    }
}