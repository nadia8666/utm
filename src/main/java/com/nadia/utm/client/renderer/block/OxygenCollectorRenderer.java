package com.nadia.utm.client.renderer.block;

import com.nadia.utm.block.entity.OxygenCollectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.model.utmPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@ForceLoad
public class OxygenCollectorRenderer extends KineticBlockEntityRenderer<OxygenCollectorBlockEntity> {
    public OxygenCollectorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(OxygenCollectorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        float ang = getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y);

        SuperByteBuffer shaft = CachedBuffers.partial(AllPartialModels.SHAFT_HALF, be.getBlockState()).rotateCentered((float) Math.toRadians(90), Direction.Axis.X);
        SuperByteBuffer fan = CachedBuffers.partial(utmPartialModels.OXYGEN_FAN, be.getBlockState()).translate(0,0.365,0);
        SuperByteBuffer grill = CachedBuffers.partial(utmPartialModels.OXYGEN_COLLECTOR_GRILL, be.getBlockState());

        // shaft is z forward apparnetly.
        kineticRotationTransform(shaft, be, Direction.Axis.Z, -ang, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        kineticRotationTransform(fan, be, Direction.Axis.Y, ang, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        grill.light(light).renderInto(ms, buffer.getBuffer(RenderType.translucent()));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(OxygenCollectorBlockEntity be, net.minecraft.world.level.block.state.BlockState state) {
        return CachedBuffers.partial(utmPartialModels.OXYGEN_FAN, state);
    }

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.OXYGEN_COLLECTOR.get(),
                OxygenCollectorRenderer::new
        ));
    }
}