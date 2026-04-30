package com.nadia.utm.block;

import com.nadia.utm.block.entity.AerowallBlockEntity;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.util.PosUtil;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class AerowallBlock extends RotatableBlock implements IBE<AerowallBlockEntity> {
    private static final VoxelShape SHAPE_NS = Block.box(0.0, 0.0, 7.5, 16.0, 16.0, 8.5);
    private static final VoxelShape SHAPE_EW = Block.box(7.5, 0.0, 0.0, 8.5, 16.0, 16.0);
    private static final VoxelShape SHAPE_UD = Block.box(0.0, 7.5, 0.0, 16.0, 8.5, 16.0);

    private static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new PlacementHelper());

    public static class PlacementHelper implements IPlacementHelper {
        @Override
        public @NotNull Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof AerowallBlock;
        }

        @Override
        public @NotNull Predicate<BlockState> getStatePredicate() {
            return state -> state.getBlock() instanceof AerowallBlock;
        }

        @Override
        public @NotNull PlacementOffset getOffset(@NotNull Player player, @NotNull Level level, BlockState state, @NotNull BlockPos pos, BlockHitResult ray) {
            List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(),
                    state.getValue(FACING)
                            .getAxis(),
                    dir -> level.getBlockState(pos.relative(dir))
                            .canBeReplaced());

            if (directions.isEmpty())
                return PlacementOffset.fail();
            else {
                return PlacementOffset.success(pos.relative(directions.getFirst()),
                        s -> s.setValue(FACING, state.getValue(FACING)));
            }
        }
    }

    public AerowallBlock(Properties properties) {
        super(properties);
    }

    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.FACING)) {
            case UP, DOWN -> SHAPE_UD;
            case EAST, WEST -> SHAPE_EW;
            case NORTH, SOUTH -> SHAPE_NS;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        Direction dir = context.getNearestLookingDirection();

        BlockPos origin = context.getClickedPos();
        if (!level.getBlockState(origin).canBeReplaced(context))
            origin = origin.relative(context.getClickedFace());

        if (context.getPlayer() != null)
            if (!context.getPlayer().isShiftKeyDown())
                for (BlockPos.MutableBlockPos pos : PosUtil.forAdjacent(origin)) {
                    BlockState block = level.getBlockState(pos);
                    if (block.is(this)) {
                        dir = block.getValue(FACING);
                        break;
                    }
                }


        return this.defaultBlockState().setValue(FACING, dir);
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
    public Class<AerowallBlockEntity> getBlockEntityClass() {
        return AerowallBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AerowallBlockEntity> getBlockEntityType() {
        return utmBlockEntities.AEROWALL.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new AerowallBlockEntity(blockPos, blockState);
    }
}
