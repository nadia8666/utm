package com.nadia.utm.block;

import com.nadia.utm.block.entity.RegenDiscBlockEntity;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

public class RegenDiscBlock extends KineticBlock implements IBE<RegenDiscBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public RegenDiscBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction dir = context.getNearestLookingDirection();

        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
            dir = dir.getOpposite();

        return this.defaultBlockState().setValue(FACING, dir);
    }


    @Override
    public Class<RegenDiscBlockEntity> getBlockEntityClass() {
        return RegenDiscBlockEntity.class;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.equals(state.getValue(FACING).getOpposite()) || face.equals(state.getValue(FACING));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(FACING).getAxis();
    }

    @Override
    public BlockEntityType<RegenDiscBlockEntity> getBlockEntityType() {
        return utmBlockEntities.REGEN_DISC.get();
    }

    protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
