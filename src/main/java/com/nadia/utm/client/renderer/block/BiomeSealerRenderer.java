package com.nadia.utm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.entity.BiomeSealerBlockEntity;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@ForceLoad(dist = Dist.CLIENT)
public class BiomeSealerRenderer extends KineticBlockEntityRenderer<BiomeSealerBlockEntity> {
    public BiomeSealerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BiomeSealerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        float ang = getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y);

        SuperByteBuffer shaft = CachedBuffers.partial(AllPartialModels.SHAFT_HALF, be.getBlockState()).rotateCentered((float) Math.toRadians(90), Direction.Axis.X);
        kineticRotationTransform(shaft, be, Direction.Axis.Z, -ang, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.BIOME_SEALER.get(),
                BiomeSealerRenderer::new
        ));
    }
}