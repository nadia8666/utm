package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.entity.CitywallsBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public class CitywallsBlockEntityRenderer implements BlockEntityRenderer<CitywallsBlockEntity> {
    private final BlockRenderDispatcher dispatcher;
    public CitywallsBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    // dont cull :D
    @Override
    public boolean shouldRenderOffScreen(@NotNull CitywallsBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 512;
    }

    public static ModelResourceLocation MRL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("utm", "block/citywalls_metal"));

    @Override
    public void render(CitywallsBlockEntity blockEntity, float partialTick, PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        stack.pushPose();

        var data = blockEntity.getModelData();
        var state = blockEntity.getBlockState();

        BakedModel bakedmodel = Minecraft.getInstance().getModelManager().getModel(MRL);
        int i = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        float f = (float)(i >> 16 & 0xFF) / 255.0F;
        float f1 = (float)(i >> 8 & 0xFF) / 255.0F;
        float f2 = (float)(i & 0xFF) / 255.0F;
        for (RenderType rt : bakedmodel.getRenderTypes(state, RandomSource.create(42), data))
            this.dispatcher.getModelRenderer().renderModel(
                            stack.last(),
                            bufferSource.getBuffer(rt),
                            state,
                            bakedmodel,
                            f,
                            f1,
                            f2,
                            packedLight,
                            packedOverlay,
                            data,
                            rt
                    );

        stack.popPose();
    }
}
