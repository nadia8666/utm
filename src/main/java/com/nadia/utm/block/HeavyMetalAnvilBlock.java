package com.nadia.utm.block;

import com.nadia.utm.registry.block.utmBlocks;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HeavyMetalAnvilBlock extends AnvilBlock {
    public HeavyMetalAnvilBlock(Properties properties) {
        super(properties);
    }

    public static BlockState damage(BlockState state) {
        return utmBlocks.HEAVY_METAL_ANVIL.block.get().defaultBlockState().setValue(FACING, state.getValue(FACING));
    }
}
