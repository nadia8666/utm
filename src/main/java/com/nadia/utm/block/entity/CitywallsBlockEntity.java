package com.nadia.utm.block.entity;

import com.nadia.utm.registry.block.utmBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CitywallsBlockEntity extends BlockEntity {
    public CitywallsBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.CITYWALLS_METAL.get(), pos, blockState);
    }
}
