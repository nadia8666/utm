package com.nadia.utm.block;

import com.mojang.serialization.MapCodec;
import com.nadia.utm.block.entity.OxygenFurnaceBlockEntity;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OxygenFurnaceBlock extends BaseEntityBlock implements IBE<OxygenFurnaceBlockEntity>, IWrenchable {
    public static final VoxelShape SHAPE = Shapes.block();
    public static final MapCodec<OxygenFurnaceBlock> CODEC = simpleCodec(OxygenFurnaceBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public OxygenFurnaceBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any());
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public Class<OxygenFurnaceBlockEntity> getBlockEntityClass() {
        return OxygenFurnaceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OxygenFurnaceBlockEntity> getBlockEntityType() {
        return utmBlockEntities.OXYGEN_FURNACE.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new OxygenFurnaceBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof OxygenFurnaceBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult) {

        if(level.getBlockEntity(pos) instanceof OxygenFurnaceBlockEntity be) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider(be, Component.translatable("utm.gui.oxygen_furnace_menu")), pos);

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.SUCCESS;
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
