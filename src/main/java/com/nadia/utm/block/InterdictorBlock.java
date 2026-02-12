package com.nadia.utm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class InterdictorBlock extends RotatableBlock {
    public InterdictorBlock(Properties properties) {
        super(properties);
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide)  { // also needs redstone signal detect
            // apply one stack of Interdicted! to all players nearby
        }

    }
}
