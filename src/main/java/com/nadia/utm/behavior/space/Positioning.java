package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

@ForceLoad
class Positioning {
    public static int getSurface(ServerLevel level, int x, int z) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, maxY, z);

        while (pos.getY() > minY) {
            if (!level.getBlockState(pos).isAir()) {
                return pos.above().getY();
            }
            pos.move(0, -1, 0);
        }

        return -13579;
    }
}
