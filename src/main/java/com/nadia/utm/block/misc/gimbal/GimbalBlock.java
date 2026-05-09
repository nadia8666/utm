package com.nadia.utm.block.misc.gimbal;

import com.nadia.utm.block.base.RotatableBlock;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.simulated_team.simulated.index.SimBlockShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GimbalBlock extends RotatableBlock implements IBE<GimbalBlockEntity>, BlockSubLevelAssemblyListener {
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");

    public GimbalBlock(Properties properties) {
        super(properties, false);

        registerDefaultState(this.stateDefinition.any().setValue(ASSEMBLED, false));
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Direction dir = context.getNearestLookingDirection().getOpposite();

        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
            dir = dir.getOpposite();

        return this.defaultBlockState().setValue(FACING, dir).setValue(ASSEMBLED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(ASSEMBLED);
    }

    @Override
    public Class<GimbalBlockEntity> getBlockEntityClass() {
        return GimbalBlockEntity.class;
    }

    @Override
    public BlockEntityType<GimbalBlockEntity> getBlockEntityType() {
        return utmBlockEntities.GIMBAL.get();
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(final @NotNull ItemStack itemStack, final @NotNull BlockState blockState, final @NotNull Level level, final @NotNull BlockPos blockPos, final Player player, final @NotNull InteractionHand interactionHand, final @NotNull BlockHitResult blockHitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.FAIL;
        }

        if (player.getItemInHand(interactionHand).isEmpty()) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }

            this.withBlockEntityDo(level, blockPos, be -> be.ASSEMBLE_NEXT_TICK = true);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull VoxelShape getShape(final BlockState blockState, final @NotNull BlockGetter blockGetter, final @NotNull BlockPos blockPos, final @NotNull CollisionContext collisionContext) {
        return blockState.getValue(ASSEMBLED) ? SimBlockShapes.SWIVEL_BEARING_ASSEMBLED.get(blockState.getValue(FACING)) : Shapes.block();
    }

    @Override
    public void beforeMove(final ServerLevel originLevel, final ServerLevel resultingLevel, final BlockState newState, final BlockPos oldPos, final BlockPos newPos) {
        this.withBlockEntityDo(originLevel, oldPos, GimbalBlockEntity::beforeAssembly);
    }

    @Override
    public void afterMove(final ServerLevel originLevel, final ServerLevel resultingLevel, final BlockState newState, final BlockPos oldPos, final BlockPos newPos) {
        this.withBlockEntityDo(resultingLevel, newPos, GimbalBlockEntity::associatePlateWithParent);
    }
}
