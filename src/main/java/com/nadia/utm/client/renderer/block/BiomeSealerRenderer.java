package com.nadia.utm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.block.entity.BiomeSealerBlockEntity;
import com.nadia.utm.block.entity.PortasealerBlockEntity;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@ForceLoad(dist = Dist.CLIENT)
public class BiomeSealerRenderer extends SafeBlockEntityRenderer<BiomeSealerBlockEntity> {
    public BiomeSealerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(BiomeSealerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {}

    static {
        utmEvents.register(EntityRenderersEvent.RegisterRenderers.class, (event) -> event.registerBlockEntityRenderer(
                utmBlockEntities.BIOME_SEALER.get(),
                BiomeSealerRenderer::new
        ));
    }
}