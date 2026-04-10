package com.nadia.utm.block;

import com.mojang.serialization.MapCodec;
import com.nadia.utm.block.entity.PortasealerBlockEntity;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PortasealerBlock extends BaseEntityBlock implements IBE<PortasealerBlockEntity>, IWrenchable {
    public static final VoxelShape SHAPE = Shapes.block();
    public static final MapCodec<OxygenFurnaceBlock> CODEC = simpleCodec(OxygenFurnaceBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public PortasealerBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any());
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public Class<PortasealerBlockEntity> getBlockEntityClass() {
        return PortasealerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PortasealerBlockEntity> getBlockEntityType() {
        return utmBlockEntities.PORTASEALER.get();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PortasealerBlockEntity(blockPos, blockState);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }
    protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

