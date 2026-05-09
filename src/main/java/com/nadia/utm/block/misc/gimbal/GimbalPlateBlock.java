package com.nadia.utm.block.misc.gimbal;

import com.nadia.utm.block.base.RotatableBlock;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GimbalPlateBlock extends RotatableBlock implements IBE<GimbalPlateBlockEntity>, BlockSubLevelAssemblyListener {
    public static VoxelShaper SHAPE = VoxelShaper.forDirectional(Block.box(5, 12, 5, 11, 16, 11), Direction.UP);

    public GimbalPlateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<GimbalPlateBlockEntity> getBlockEntityClass() {
        return GimbalPlateBlockEntity.class;
    }

    @Override
    public BlockEntityType<GimbalPlateBlockEntity> getBlockEntityType() {
        return utmBlockEntities.GIMBAL_PLATE.get();
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(final BlockState blockState, final @NotNull BlockGetter blockGetter, final @NotNull BlockPos blockPos, final @NotNull CollisionContext collisionContext) {
        return SHAPE.get(blockState.getValue(FACING));
    }

    @Override
    protected @NotNull VoxelShape getShape(final BlockState blockState, final @NotNull BlockGetter blockGetter, final @NotNull BlockPos blockPos, final @NotNull CollisionContext collisionContext) {
        return SHAPE.get(blockState.getValue(FACING));
    }

    @Override
    public void beforeMove(final ServerLevel originLevel, final ServerLevel resultingLevel, final BlockState newState, final BlockPos oldPos, final BlockPos newPos) {
        this.withBlockEntityDo(originLevel, oldPos, GimbalPlateBlockEntity::beforeAssembly);
    }

    @Override
    public void afterMove(final ServerLevel originLevel, final ServerLevel resultingLevel, final BlockState newState, final BlockPos oldPos, final BlockPos newPos) {
        this.withBlockEntityDo(resultingLevel, newPos, GimbalPlateBlockEntity::fixParentLinkingWhenMoved);
    }
}
