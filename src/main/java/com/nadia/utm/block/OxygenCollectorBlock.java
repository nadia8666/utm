package com.nadia.utm.block;

import com.nadia.utm.block.entity.OxygenCollectorBlockEntity;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class OxygenCollectorBlock extends KineticBlock implements IBE<OxygenCollectorBlockEntity> {
    public OxygenCollectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    public Class<OxygenCollectorBlockEntity> getBlockEntityClass() {
        return OxygenCollectorBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return Direction.Axis.Y;
    }

    @Override
    public BlockEntityType<OxygenCollectorBlockEntity> getBlockEntityType() {
        return utmBlockEntities.OXYGEN_COLLECTOR.get();
    }
}
