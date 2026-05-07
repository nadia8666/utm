package com.nadia.utm.block.propulsion.liquid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.client.renderer.IBlockstateRotatedRenderer;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.model.utmModels;
import com.nadia.utm.util.PoseUtil;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

@ForceLoad(dist = Dist.CLIENT)
public class LiquidFuelThrusterRenderer extends SmartBlockEntityRenderer<LiquidFuelThrusterBlockEntity> implements IBlockstateRotatedRenderer {
    public LiquidFuelThrusterRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull LiquidFuelThrusterBlockEntity blockEntity) {
        return true;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull LiquidFuelThrusterBlockEntity blockEntity) {
        return AABB.ofSize(Vec3.atCenterOf(blockEntity.getBlockPos()), 6, 6, 6);
    }

    @Override
    public void renderSafe(@NotNull LiquidFuelThrusterBlockEntity be, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffer, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null) return;

        SuperByteBuffer thrustMain = CachedBuffers.partial(utmModels.THRUST_LARGE_1, be.getBlockState());
        SuperByteBuffer thrustAlt = CachedBuffers.partial(utmModels.THRUST_LARGE_2, be.getBlockState());

        new PoseUtil(ms).push().run(() -> rotateByFacing(be, ms)).run(() -> {
            Direction facing = be.getBlockState().getValue(LiquidFuelThrusterBlock.FACING);
            Direction.Axis axis = facing.getAxis();

            double scalar = ((double) level.getGameTime() + partialTicks / 20) * be.getThrust() / 4;
            float alpha = be.getThrust() / LiquidFuelThrusterBlockEntity.THRUST_MAX;
            float scale = 1f * ((float) Math.sin(scalar / 10) / 100 + 1) * alpha;

            RenderType rt = VeilRenderType.get(utmRenderTypes.THRUST_REGULAR);
            RenderType rt2 = VeilRenderType.get(utmRenderTypes.THRUST_REGULAR_DEEP);

            if (rt != null && rt2 != null) {
                thrustMain.light(light)
                        .translate(0.5f, 0.5f, 0.0f)
                        .scale(scale)
                        .scaleZ(1.8f * alpha)
                        .translate(-0.5f, -0.5f, 0.0f)
                        .renderInto(ms, buffer.getBuffer(rt));

                thrustAlt.light(light)
                        .translate(0.5f, 0.5f, 0.0f)
                        .scale(scale * .7f)
                        .scaleZ(2.23f * alpha)
                        .translate(-0.5f, -0.5f, 0.0f)
                        .renderInto(ms, buffer.getBuffer(rt2));
            }
        }).pop();
    }

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.LIQUID_THRUSTER.get(),
                LiquidFuelThrusterRenderer::new
        ));
    }
}
