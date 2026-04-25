package com.nadia.utm.util;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SableUtil {
    /**
     * get sable sublevel chunk via world (global) pos
     * @param level sublevel
     * @param pos position
     * @return chunk
     */
    public static LevelChunk getChunkWorldPos(SubLevel level, BlockPos pos) {
        LevelPlot plot = level.getPlot();
        return plot.getChunk(plot.toLocal(new ChunkPos(toLocalPos(level.logicalPose(), pos))));
    }

    /**
     * get sable sublevel chunk via sable (local) pos
     * @param level sublevel
     * @param pos position
     * @return chunk
     */
    public static LevelChunk getChunkLocalPos(SubLevel level, BlockPos pos) {
        LevelPlot plot = level.getPlot();
        return plot.getChunk(plot.toLocal(new ChunkPos(pos)));
    }

    public static BlockPos toBlockPos(Vec3 pos) {
        return BlockPos.containing(pos);
    }

    public static Vec3 toVec(BlockPos pos) {
        return Vec3.atCenterOf(pos);
    }

    /**
     * converts world (global) space pos to sable (local) space pos
     *
     * @param pose sable pose
     * @param pos  position
     * @return sable (local) space pos
     */
    public static BlockPos toLocalPos(Pose3d pose, BlockPos pos) {
        return toBlockPos(pose.transformPositionInverse(toVec(pos)));
    }

    /**
     * converts sable (local) space pos to world (global) space pos
     *
     * @param pose sable pose
     * @param pos  position
     * @return world (global) space pos
     */
    public static BlockPos toGlobalPos(Pose3d pose, BlockPos pos) {
        return toBlockPos(pose.transformPosition(toVec(pos)));
    }

    /**
     * get blockstate for a block in a sable sublevel
     *
     * @param level sable sublevel
     * @param pos   block position in sable (local) space
     * @return blockstate or null if there is no chunk
     */
    @Nullable
    public static BlockState getState(SubLevel level, BlockPos pos) {
        ChunkAccess chunk = SableUtil.getChunkLocalPos(level, pos);
        if (chunk != null) {
            return chunk.getBlockState(pos);
        }

        return null;
    }
}
