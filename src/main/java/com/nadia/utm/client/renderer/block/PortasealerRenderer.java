package com.nadia.utm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.entity.PortasealerBlockEntity;
import com.nadia.utm.client.renderer.IBlockstateRotatedRenderer;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.util.PoseUtil;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.fluids.FluidStack;

@ForceLoad(dist = Dist.CLIENT)
public class PortasealerRenderer extends SafeBlockEntityRenderer<PortasealerBlockEntity> implements IBlockstateRotatedRenderer {
    public PortasealerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(PortasealerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FluidStack oxygenStack = be.TANK.getPrimaryHandler().getFluid();
        new PoseUtil(ms).push().run(() -> rotateByState(be, ms, -90f)).run(() -> drawTank(oxygenStack, buffer, ms, light)).pop();
    }

    private void drawTank(FluidStack stack, MultiBufferSource buffer, PoseStack ms, int light) {
        if (stack.isEmpty()) return;

        new PoseUtil(ms).push().run(() -> NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(stack, 0.575f, 0.375f, 0.03125f, 0.6375f, 0.625f, 0.09375f, buffer, ms, light, true, true)).pop();
    }

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.PORTASEALER.get(),
                PortasealerRenderer::new
        ));
    }
}