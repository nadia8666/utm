package com.nadia.utm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class InterdictorBlock extends RotatableBlock {
    public InterdictorBlock(Properties properties) {
        super(properties);
    }

    protected void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide)  { // also needs redstone signal detect
            // apply one stack of Interdicted! to all players nearby
        }

    }
}
