package com.nadia.utm.registry.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;

@SuppressWarnings("RedundantCast")
public class PartialEntityModelRenderer {
    private static final RandomSource random = RandomSource.create();

    public static void render(PoseStack ms, BakedModel model, RenderType type, MultiBufferSource source, int light, int overlay) {
        VertexConsumer vc = source.getBuffer(type);

        for (BakedModel pass : model.getRenderPasses((ItemStack) null, false)) {
            render(pass, light, overlay, ms, vc, type);
        }
    }

    private static void render(BakedModel model, int light, int overlay, PoseStack ms, VertexConsumer buffer, RenderType type) {
        ModelData data = ModelData.EMPTY;

        for (Direction direction : Iterate.directions) {
            random.setSeed(42L);
            renderQuadList(ms, buffer, model.getQuads(null, direction, random, data, type), light, overlay);
        }

        random.setSeed(42L);
        renderQuadList(ms, buffer, model.getQuads(null, null, random, data, type), light, overlay);
    }

    private static void renderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, int combinedLight, int combinedOverlay) {
        PoseStack.Pose pose = poseStack.last();

        for (BakedQuad bakedquad : quads)
            buffer.putBulkData(pose, bakedquad, 1, 1, 1, 1, combinedLight, combinedOverlay, true);
    }
}
