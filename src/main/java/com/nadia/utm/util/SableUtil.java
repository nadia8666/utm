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
    public static LevelChunk getChunkWorldPos(SubLevel level, BlockPos pos) {
        LevelPlot plot = level.getPlot();
        return plot.getChunk(plot.toLocal(new ChunkPos(toSublevelPos(level.logicalPose(), pos))));
    }

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

    public static BlockPos toSublevelPos(Pose3d pose, BlockPos pos) {
        return toBlockPos(pose.transformPositionInverse(toVec(pos)));
    }

    public static BlockPos toWorldPos(Pose3d pose, BlockPos pos) {
        return toBlockPos(pose.transformPosition(toVec(pos)));
    }

    @Nullable
    public static BlockState getState(SubLevel level, BlockPos pos) {
        ChunkAccess chunk = SableUtil.getChunkLocalPos(level, pos);
        if (chunk != null) {
            return chunk.getBlockState(pos);
        }

        return null;
    }
}
