package com.nadia.utm.block.propulsion.solid;

import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

public class SolidThrusterBlock extends Block implements IBE<SolidThrusterBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public SolidThrusterBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction dir = context.getNearestLookingDirection();

        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
            dir = dir.getOpposite();

        return this.defaultBlockState().setValue(FACING, dir);
    }


    @Override
    public Class<SolidThrusterBlockEntity> getBlockEntityClass() {
        return SolidThrusterBlockEntity.class;
    }

    @Override
    public BlockEntityType<SolidThrusterBlockEntity> getBlockEntityType() {
        return utmBlockEntities.SOLID_THRUSTER.get();
    }

    protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
