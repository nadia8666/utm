package com.nadia.utm.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PosUtil {
    public static Iterable<BlockPos.MutableBlockPos> forAdjacent(BlockPos origin) {
        return () -> new Iterator<>() {
            private int index = 0;
            private final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            private final Direction[] directions = Direction.values();

            @Override
            public boolean hasNext() {
                return index < 6;
            }

            @Override
            public BlockPos.MutableBlockPos next() {
                mutable.setWithOffset(origin, directions[index++]);
                return mutable;
            }
        };
    }
    public static List<BlockPos> getAdjacent(BlockPos origin) {
        List<BlockPos> list = new ArrayList<>(6);
        for (BlockPos.MutableBlockPos pos : forAdjacent(origin)) {
            list.add(pos.immutable());
        }
        return list;
    }
}
