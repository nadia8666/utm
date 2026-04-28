package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

// til this is not a default feature. and create just does this for a specific subclass of their renderer
public interface IBlockstateRotatedRenderer {
    default void rotateByHoriz(BlockEntity be, PoseStack ms, @Nullable Float offset) {
        Direction facing = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        ms.translate(0.5f, 0.5f, 0.5f);
        ms.mulPose(Axis.YP.rotationDegrees((offset != null ? offset : 180f) - facing.toYRot()));
        ms.translate(-0.5f, -0.5f, -0.5f);
    }

    default void rotateByHoriz(BlockEntity be, PoseStack ms) {
        rotateByHoriz(be, ms, null);
    }

    default void rotateByFacing(BlockEntity be, PoseStack ms) {
        Direction facing = be.getBlockState().getValue(BlockStateProperties.FACING);

        ms.translate(0.5f, 0.5f, 0.5f);

        switch (facing) {
            case DOWN -> ms.mulPose(Axis.XP.rotationDegrees(90f));
            case UP -> ms.mulPose(Axis.XP.rotationDegrees(-90f));
            case NORTH -> ms.mulPose(Axis.YP.rotationDegrees(180f));
            case SOUTH -> ms.mulPose(Axis.YP.rotationDegrees(0f));
            case WEST -> ms.mulPose(Axis.YP.rotationDegrees(90f));
            case EAST -> ms.mulPose(Axis.YP.rotationDegrees(270f));
        }

        ms.translate(-0.5f, -0.5f, -0.5f);
    }

}
