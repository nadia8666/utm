package com.nadia.utm.mixin.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.client.renderer.utmShaders;
import com.simibubi.create.foundation.item.LayeredArmorItem;
import com.simibubi.create.foundation.mixin.accessor.HumanoidArmorLayerAccessor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(value = LayeredArmorItem.class, remap = false)
public interface LayeredArmorItemMixin {
    /**
     * @author nadiarr
     * @reason forward port
     */
    @Overwrite
    private void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int light, ArmorItem item,
                             Model model, boolean glint, int color, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, color);
        if (glint)
            model.renderToBuffer(poseStack, bufferSource.getBuffer(GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ENTITY.get() : utmRenderTypes.OVERLAY_GLINT_ENTITY.get()), light, OverlayTexture.NO_OVERLAY);
    }


    /**
     * @author nadiarr
     * @reason hate
     */
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Overwrite
    default void renderArmorPiece(HumanoidArmorLayer<?, ?, ?> layer, PoseStack poseStack,
                                  MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light,
                                  HumanoidModel<?> originalModel, ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem item)) {
            return;
        }
        if (!item.canEquip(stack, slot, entity)) {
            return;
        }

        if (bufferSource instanceof MultiBufferSource.BufferSource source) {
            var changed = GLINT_COLOR.passUpdate(stack, source, false);
            changed = GLINT_LOCATION.passUpdate(stack, source, changed);
            changed = GLINT_SPEED.passUpdate(stack, source, changed);
            changed = GLINT_SCALE.passUpdate(stack, source, changed);
            changed = GLINT_ADDITIVE.passUpdate(stack, source, changed);

            if (changed) {
                int color = GLINT_COLOR.THREAD.get();
                setGlintColor(color != -1 ? color : DEFAULT_COLOR, GLINT_ADDITIVE.THREAD.get() ? utmShaders.GLINT_ADDITIVE : utmShaders.GLINT_OVERLAY);
            }
        }

        HumanoidArmorLayerAccessor accessor = (HumanoidArmorLayerAccessor) layer;
        Map<String, ResourceLocation> locationCache = HumanoidArmorLayerAccessor.create$getArmorLocationCache();
        boolean glint = stack.hasFoil();

        HumanoidModel<?> innerModel = accessor.create$getInnerModel();
        layer.getParentModel().copyPropertiesTo((HumanoidModel) innerModel);
        accessor.create$callSetPartVisibility(innerModel, slot);
        String locationStr2 = getArmorTextureLocation(entity, slot, stack, 2);
        ResourceLocation location2 = locationCache.computeIfAbsent(locationStr2, ResourceLocation::parse);
        renderModel(poseStack, bufferSource, light, item, innerModel, glint, -1, location2);

        HumanoidModel<?> outerModel = accessor.create$getOuterModel();
        layer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
        accessor.create$callSetPartVisibility(outerModel, slot);
        String locationStr1 = getArmorTextureLocation(entity, slot, stack, 1);
        ResourceLocation location1 = locationCache.computeIfAbsent(locationStr1, ResourceLocation::parse);
        renderModel(poseStack, bufferSource, light, item, outerModel, glint, -1, location1);
    }

    @Unique
    String getArmorTextureLocation(LivingEntity entity, EquipmentSlot slot, ItemStack stack, int layer);
}
