package com.nadia.utm.block;

import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HeavyMetalAnvilBlock extends AnvilBlock {
    private static final float FALL_DAMAGE_PER_DISTANCE = 0F;
    public HeavyMetalAnvilBlock(Properties properties) {
        super(properties);
    }

    public static BlockState damage(BlockState state) {
        return state;
    }
}
