package com.nadia.utm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.entity.OxygenFurnaceBlockEntity;
import com.nadia.utm.client.renderer.IBlockstateRotatedRenderer;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.model.utmModels;
import com.nadia.utm.util.PoseUtil;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.fluids.FluidStack;

@ForceLoad(dist = Dist.CLIENT)
public class OxygenFurnaceRenderer extends SafeBlockEntityRenderer<OxygenFurnaceBlockEntity> implements IBlockstateRotatedRenderer {
    public OxygenFurnaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(OxygenFurnaceBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FluidStack oxygenStack = be.LOX.getPrimaryHandler().getFluid();
        FluidStack steelStack = be.STEEL.getPrimaryHandler().getFluid();
        SuperByteBuffer tanks = CachedBuffers.partial(utmModels.OXYGEN_FURNACE_TANKS, be.getBlockState());

        new PoseUtil(ms).push().run(() -> rotateByHoriz(be, ms)).run(() -> {
            drawTank(oxygenStack, buffer, ms, light, 11, 1 / 64f, 16 - 1 / 64f, 6 - 1 / 64f);
            drawTank(steelStack, buffer, ms, light, 1 / 64f, 6 + 1 / 64f, 4 - 1 / 64f, 16 - 1 / 64f);

            tanks.light(light).translate(0, 1 / 128f, 0).renderInto(ms, buffer.getBuffer(utmRenderTypes.TRANSLUCENT_NO_CULL));
        }).pop();
    }

    private static float lerp(float b, float c) {
        return 4.8828125E-4F + (b - 4.8828125E-4F) * c;
    }

    private void drawTank(FluidStack stack, MultiBufferSource buffer, PoseStack ms, int light, float x1, float z1, float y2, float z2) {
        if (stack.isEmpty()) return;

        new PoseUtil(ms).push().run(() -> NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(stack, x1 / 16, 4.8828125E-4F, z1 / 16, 15.9921875f / 16, lerp(y2 / 16, ((float) stack.getAmount()) / 1000F), z2 / 16, buffer, ms, light, true, true)).pop();
    }

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.OXYGEN_FURNACE.get(),
                OxygenFurnaceRenderer::new
        ));
    }
}