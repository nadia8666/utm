package com.nadia.utm.block;

import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GrateBlock extends Block implements IWrenchable, SimpleWaterloggedBlock {
    public static DirectionProperty VERTICAL_DIRECTION = DirectionProperty.create("vertical_direction", Direction.UP, Direction.DOWN);
    private static final VoxelShape BOTTOM;
    private static final VoxelShape TOP;
    public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public GrateBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any().setValue(VERTICAL_DIRECTION, Direction.UP).setValue(WATERLOGGED, false));
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VERTICAL_DIRECTION, WATERLOGGED);
    }

    @Override
    protected @NotNull BlockState updateShape(
            BlockState state,
            @NotNull Direction direction,
            @NotNull BlockState neighborState,
            @NotNull LevelAccessor level,
            @NotNull BlockPos pos,
            @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }



    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(VERTICAL_DIRECTION).getAxisDirection() == Direction.AxisDirection.NEGATIVE ? BOTTOM : TOP;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos pos = context.getClickedPos();

        if (clickedFace == Direction.UP) {
            return this.defaultBlockState().setValue(VERTICAL_DIRECTION, Direction.DOWN);
        }

        if (clickedFace == Direction.DOWN) {
            return this.defaultBlockState().setValue(VERTICAL_DIRECTION, Direction.UP);
        }

        double y = context.getClickLocation().y - (double) pos.getY();
        return this.defaultBlockState().setValue(VERTICAL_DIRECTION, y > 0.5 ? Direction.UP : Direction.DOWN);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!level.isClientSide) {
            Direction currentFacing = state.getValue(VERTICAL_DIRECTION);
            Direction newFacing = (currentFacing == Direction.UP) ? Direction.DOWN : Direction.UP;

            level.setBlock(pos, state.setValue(VERTICAL_DIRECTION, newFacing), 3);
        }

        return InteractionResult.SUCCESS;
    }

    static {
        BOTTOM = Block.box(0, 0.0, 0, 16, 1, 16);
        TOP = Block.box(0, 15.0, 0, 16, 16, 16);

        RadialWrenchMenu.registerRotationProperty(VERTICAL_DIRECTION, "Vertical Direction");
    }
}
