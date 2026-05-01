package com.nadia.utm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.IonJetBlock;
import com.nadia.utm.block.entity.IonJetBlockEntity;
import com.nadia.utm.client.renderer.IBlockstateRotatedRenderer;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.model.utmModels;
import com.nadia.utm.util.PoseUtil;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

@ForceLoad(dist = Dist.CLIENT)
public class IonJetRenderer extends KineticBlockEntityRenderer<IonJetBlockEntity> implements IBlockstateRotatedRenderer {
    public IonJetRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderSafe(@NotNull IonJetBlockEntity be, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffer, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null) return;

        SuperByteBuffer shaft = CachedBuffers.partial(AllPartialModels.SHAFT_HALF, be.getBlockState()).rotateCentered((float) Math.toRadians(-90), Direction.Axis.X);
        SuperByteBuffer bottom1 = CachedBuffers.partial(utmModels.ION_JET_BOTTOM_1, be.getBlockState());
        SuperByteBuffer bottom2 = CachedBuffers.partial(utmModels.ION_JET_BOTTOM_2, be.getBlockState());
        SuperByteBuffer vent = CachedBuffers.partial(utmModels.ION_JET_VENT, be.getBlockState());
        SuperByteBuffer cogs = CachedBuffers.partial(utmModels.ION_JET_COGS, be.getBlockState());


        new PoseUtil(ms).push().run(() -> rotateByFacing(be, ms)).run(() -> {
            Direction facing = be.getBlockState().getValue(IonJetBlock.FACING);
            Direction.Axis axis = facing.getAxis();
            float ang = getAngleForBe(be, be.getBlockPos(), axis);
            if (facing == Direction.DOWN || facing == Direction.NORTH || facing == Direction.WEST) {
                ang = -ang;
            }

            shaft.rotateCentered((float) Math.toRadians(90), Direction.Axis.X);
            bottom1.rotateCentered((float) Math.toRadians(90), Direction.Axis.X);
            bottom2.rotateCentered((float) Math.toRadians(90), Direction.Axis.X);
            vent.rotateCentered((float) Math.toRadians(90), Direction.Axis.X);
            cogs.rotateCentered((float) Math.toRadians(90), Direction.Axis.X);

            kineticRotationTransform(shaft, be, Direction.Axis.Z, ang, light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
            bottom1.light(light).translate(0, Math.sin(((double) level.getGameTime() / 3 + partialTicks / 20) * be.getThrust()) / 40, 0).renderInto(ms, buffer.getBuffer(RenderType.solid()));
            bottom2.light(light).translate(0, Math.sin((level.getGameTime() + partialTicks / 20) * 3 * be.getThrust()) / 60, 0).renderInto(ms, buffer.getBuffer(RenderType.solid()));
            vent.light(light).renderInto(ms, buffer.getBuffer(RenderType.translucent()));
            cogs.light(light).rotateCentered(ang, Direction.Axis.Y).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        }).pop();
    }

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.ION_JET.get(),
                IonJetRenderer::new
        ));
    }
}
