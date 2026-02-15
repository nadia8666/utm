package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.entity.CitywallsBlockEntity;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.utm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CitywallsBlockEntityRenderer implements BlockEntityRenderer<CitywallsBlockEntity> {
    private final BlockRenderDispatcher dispatcher;
    public CitywallsBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull CitywallsBlockEntity blockEntity) {
        return true;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull CitywallsBlockEntity blockEntity) {
        return AABB.ofSize(Vec3.atLowerCornerOf(blockEntity.getBlockPos()), 140, 140, 140); // i checked the size manually (its diameter). so . i think it's too big but idc
    }

    @Override
    public int getViewDistance() {
        return 512;
    }

    public int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight,sLight);
    }
    public static final ModelResourceLocation CWL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("utm", "block/citywalls_metal"));
    public static final ModelResourceLocation OWM = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("utm", "block/outpostwalls_metal"));
    public static final ModelResourceLocation OWS = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("utm", "block/outpostwalls_shrine"));

    @Override
    public void render(CitywallsBlockEntity blockEntity, float partialTick, PoseStack stack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        stack.pushPose();

        var data = blockEntity.getModelData();
        var state = blockEntity.getBlockState();
        var model = Minecraft.getInstance().getModelManager().getModel(CWL);
        if (state.getBlock().toString().equals("Block{utm:outpostwalls_shrine}")) {
            stack.translate(0, 1, 0);
            model = Minecraft.getInstance().getModelManager().getModel(OWS);
        } else if (state.getBlock().toString().equals("Block{utm:outpostwalls_metal}")) {
            stack.translate(0, 5, 0);
        model = Minecraft.getInstance().getModelManager().getModel(OWM);
        }

        int i = Minecraft.getInstance().getBlockColors().getColor(state, blockEntity.getLevel(), blockEntity.getBlockPos(), 0);
        float f = (float)(i >> 16 & 0xFF) / 255.0F;
        float f1 = (float)(i >> 8 & 0xFF) / 255.0F;
        float f2 = (float)(i & 0xFF) / 255.0F;
        for (RenderType rt : model.getRenderTypes(state, RandomSource.create(42), data))

            this.dispatcher.getModelRenderer().renderModel(
                    stack.last(),
                    bufferSource.getBuffer(rt),
                    state,
                    model,
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
