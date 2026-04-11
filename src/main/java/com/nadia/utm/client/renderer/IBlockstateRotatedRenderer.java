package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

// til this is not a default feature. and create just does this for a specific subclass of their renderer
public interface IBlockstateRotatedRenderer {
    default void rotateByState(BlockEntity be, PoseStack ms, @Nullable Float offset) {
        Direction facing = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        ms.translate(0.5f, 0.5f, 0.5f);
        ms.mulPose(Axis.YP.rotationDegrees((offset != null ? offset : 180f) - facing.toYRot()));
        ms.translate(-0.5f, -0.5f, -0.5f);
    }

    default void rotateByState(BlockEntity be, PoseStack ms) {
        rotateByState(be, ms, null);
    }
}
