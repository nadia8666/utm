package com.nadia.utm.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class GrateBlock extends Block implements IWrenchable, SimpleWaterloggedBlock {
    public static final DirectionProperty VERTICAL_DIRECTION = DirectionProperty.create("vertical_direction", Direction.UP, Direction.DOWN);
    private static final VoxelShape BOTTOM;
    private static final VoxelShape TOP;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new GrateBlock.PlacementHelper());

    public static class PlacementHelper implements IPlacementHelper {
        @Override
        public @NotNull Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof GrateBlock;
        }

        @Override
        public @NotNull Predicate<BlockState> getStatePredicate() {
            return state -> state.getBlock() instanceof GrateBlock;
        }

        @Override
        public @NotNull PlacementOffset getOffset(@NotNull Player player, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, BlockHitResult ray) {
            List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(),
                    Direction.Axis.Y,
                    dir -> level.getBlockState(pos.relative(dir))
                            .canBeReplaced());

            if (directions.isEmpty())
                return PlacementOffset.fail();
            else {
                return PlacementOffset.success(pos.relative(directions.getFirst()),
                        s -> s.setValue(VERTICAL_DIRECTION, state.getValue(VERTICAL_DIRECTION)));
            }
        }
    }

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
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        IPlacementHelper placementHelper = PlacementHelpers.get(PLACEMENT_HELPER_ID);
        if (!player.isShiftKeyDown() && player.mayBuild()) {
            if (placementHelper.matchesItem(stack)) {
                placementHelper.getOffset(player, level, state, pos, hitResult)
                        .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
    }
}
