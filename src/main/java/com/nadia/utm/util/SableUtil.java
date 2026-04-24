package com.nadia.utm.util;

import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.utm;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class SableUtil {
    public static LevelChunk getChunkWorldPos(LevelPlot plot, BlockPos pos) {
        return plot.getChunk(plot.toLocal(new ChunkPos(pos)));
    }

    public static LevelChunk getChunkWorldPos(SubLevel level, BlockPos pos) {
        return getChunkWorldPos(level.getPlot(), pos);
    }

    public static LevelChunk getChunkLocalPos(SubLevel level, BlockPos pos) {
        return level.getPlot().getChunk(new ChunkPos(pos));
    }

    public static BlockPos toBlockPos(Vec3 pos) {
        return BlockPos.containing(pos);
    }

    public static Vec3 toVec(BlockPos pos) {
        return Vec3.atLowerCornerOf(pos);
    }

    public static BlockPos localize(Pose3d pose, BlockPos pos) {
        return toBlockPos(pose.transformPositionInverse(toVec(pos)));
    }

    public static BlockState getState(LevelPlot plot, BlockPos pos) {
        BlockState state = Blocks.AIR.defaultBlockState();
        for (PlotChunkHolder c : plot.getLoadedChunks()) {
            if (state.isAir())
                state = c.getChunk().getBlockState(pos);
            utm.LOGGER.info("[UTM] ship chunk pos {}, SEALINFO {}", c.getPos(), c.getChunk().getData(utmAttachments.SEALED_AIR));
        }

        return state;
    }
}
