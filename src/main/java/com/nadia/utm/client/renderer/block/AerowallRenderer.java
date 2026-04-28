package com.nadia.utm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.entity.AerowallBlockEntity;
import com.nadia.utm.client.renderer.IBlockstateRotatedRenderer;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.model.utmModels;
import com.nadia.utm.util.PoseUtil;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

@ForceLoad(dist = Dist.CLIENT)
public class AerowallRenderer implements BlockEntityRenderer<AerowallBlockEntity>, IBlockstateRotatedRenderer {
    public AerowallRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull AerowallBlockEntity be, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffer, int light, int overlay) {
        RenderType rt = VeilRenderType.get(utmRenderTypes.AEROWALL);
        if (rt == null) return;
        SuperByteBuffer wall = CachedBuffers.partial(utmModels.AEROWALL, be.getBlockState());

        new PoseUtil(ms).push().run(() -> rotateByFacing(be, ms)).run(() -> {
            wall.light(light).renderInto(ms, buffer.getBuffer(rt));
        }).pop();
    }

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.AEROWALL.get(),
                AerowallRenderer::new
        ));
    }
}
