package com.nadia.utm.block.misc.large_basin;

import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class LargeBasinBlock extends BasinBlock {
    public LargeBasinBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state) {
        super.createBlockStateDefinition(state);
    }

    public static boolean isBasin(LevelReader world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof BasinBlockEntity;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.above());
        return !(blockEntity instanceof BasinOperatingBlockEntity);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!context.getLevel().isClientSide)
            withBlockEntityDo(context.getLevel(), context.getClickedPos(),
                    bte -> bte.onWrenched(context.getClickedFace()));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        return onBlockEntityUseItemOn(level, pos, be -> {
            if (!stack.isEmpty()) {
                if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be))
                    return ItemInteractionResult.SUCCESS;
                if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be))
                    return ItemInteractionResult.SUCCESS;

                if (GenericItemEmptying.canItemBeEmptied(level, stack)
                        || GenericItemFilling.canItemBeFilled(level, stack))
                    return ItemInteractionResult.SUCCESS;
                if (stack.getItem().equals(Items.SPONGE)) {
                    IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
                    if (fluidHandler != null) {
                        FluidStack drained = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                        if (!drained.isEmpty()) {
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            IItemHandlerModifiable inv = ((LargeBasinBlockEntity) be).getItemCapability();
            if (inv == null)
                inv = new ItemStackHandler(1);
            boolean success = false;
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stackInSlot = inv.getStackInSlot(slot);
                if (stackInSlot.isEmpty())
                    continue;
                player.getInventory()
                        .placeItemBackInInventory(stackInSlot);
                inv.setStackInSlot(slot, ItemStack.EMPTY);
                success = true;
            }
            if (success)
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                        1f + level.getRandom().nextFloat());
            be.onEmptied();
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public void updateEntityAfterFallOn(@NotNull BlockGetter worldIn, @NotNull Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!worldIn.getBlockState(entityIn.blockPosition())
                .is(this))
            return;
        if (!(entityIn instanceof ItemEntity itemEntity))
            return;
        if (!entityIn.isAlive())
            return;
        withBlockEntityDo(worldIn, entityIn.blockPosition(), be -> {
            ItemStack insertItem = ItemHandlerHelper.insertItem(be.inputInventory, itemEntity.getItem()
                    .copy(), false);
            if (insertItem.isEmpty()) {
                itemEntity.discard();
                return;
            }
            itemEntity.setItem(insertItem);
        });
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos) {
        return AllShapes.BASIN_RAYTRACE_SHAPE;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return AllShapes.BASIN_BLOCK_SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        if (ctx instanceof EntityCollisionContext && ((EntityCollisionContext) ctx).getEntity() instanceof ItemEntity)
            return AllShapes.BASIN_COLLISION_SHAPE;
        return getShape(state, reader, pos, ctx);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level worldIn, @NotNull BlockPos pos) {
        return getBlockEntityOptional(worldIn, pos).map(BasinBlockEntity::getInputInventory)
                .map(ItemHelper::calcRedstoneFromInventory)
                .orElse(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<BasinBlockEntity> getBlockEntityClass() {
        return (Class<BasinBlockEntity>) (Object) LargeBasinBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LargeBasinBlockEntity> getBlockEntityType() {
        return utmBlockEntities.LARGE_BASIN.get();
    }

    public static boolean canOutputTo(BlockGetter world, BlockPos basinPos, Direction direction) {
        BlockPos neighbour = basinPos.relative(direction);
        BlockPos output = neighbour.below();
        BlockState blockState = world.getBlockState(neighbour);

        if (FunnelBlock.isFunnel(blockState)) {
            if (FunnelBlock.getFunnelFacing(blockState) == direction)
                return false;
        } else if (!blockState.getCollisionShape(world, neighbour)
                .isEmpty()) {
            return false;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(output);
            if (blockEntity instanceof BeltBlockEntity belt) {
                return belt.getSpeed() == 0 || belt.getMovementFacing() != direction.getOpposite();
            }
        }

        DirectBeltInputBehaviour directBeltInputBehaviour =
                BlockEntityBehaviour.get(world, output, DirectBeltInputBehaviour.TYPE);
        if (directBeltInputBehaviour != null)
            return directBeltInputBehaviour.canInsertFromSide(direction);
        return false;
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
        return false;
    }
}
