package com.nadia.utm.block;

import com.google.common.collect.Iterables;
import com.nadia.utm.block.entity.AbstractSealerBlockEntity;
import com.nadia.utm.util.PosUtil;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AerowallBlock extends RotatableBlock {
    public AerowallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        Direction dir = context.getNearestLookingDirection();

        assert context.getPlayer() != null;
        if (!context.getPlayer().isShiftKeyDown())
            for (BlockPos pos : PosUtil.forAdjacent(context.getClickedPos().relative(context.getClickedFace()))) {
                BlockState block = level.getBlockState(pos);
                if (block.is(this)) {
                    dir = block.getValue(FACING);
                    break;
                }
            }

        return this.defaultBlockState().setValue(FACING, dir);
    }
}
