package com.nadia.utm.block;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class HeavyMetalAnvilBlock extends AnvilBlock {
    public HeavyMetalAnvilBlock(Properties properties) {
        super(properties);
    }

    public static BlockState damage(BlockState state) {
        return state;
    }

    @Override
    public void onLand(@NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull BlockState state, @NotNull BlockState replaceableState, @NotNull FallingBlockEntity fallingBlock) {
        super.onLand(level, pos, state, replaceableState, fallingBlock);
    }
}
