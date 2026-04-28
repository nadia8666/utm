package com.nadia.utm.block;

import com.nadia.utm.block.entity.AerowallBlockEntity;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.util.PosUtil;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AerowallBlock extends RotatableBlock implements IBE<AerowallBlockEntity> {
    public AerowallBlock(Properties properties) {
        super(properties);
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
