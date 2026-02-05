package com.nadia.utm.mixin.renderer.glint;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.client.renderer.utmShaders;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;

@Mixin(value = HumanoidArmorLayer.class, remap = false, priority = 2000)
public class FiguraCompatMixin<T extends LivingEntity, A extends HumanoidModel<T>> {
    // shh, it's okay little mixin. the errors are intentional you see?
    @Inject(
            method = "figura$renderArmorPart",
            at= @At(value = "HEAD"),
            remap = false
    )
    public void utm$figura(
            ModelPart modelPart,
            PoseStack poseStack,
            MultiBufferSource vertexConsumers,
            int light,
            LivingEntity entity,
            ItemStack itemStack,
            EquipmentSlot armorSlot,
            ArmorItem armorItem,
            CallbackInfo ci) {
        if (itemStack.hasFoil()) {
            if (vertexConsumers instanceof MultiBufferSource.BufferSource bufferSource) {
                boolean changed;
                changed = GLINT_COLOR.passUpdate(itemStack, bufferSource, false);
                changed = GLINT_LOCATION.passUpdate(itemStack, bufferSource, changed);
                changed = GLINT_SPEED.passUpdate(itemStack, bufferSource, changed);
                changed = GLINT_SCALE.passUpdate(itemStack, bufferSource, changed);
                changed = GLINT_ADDITIVE.passUpdate(itemStack, bufferSource, changed);

                if (changed) {
                    int color = GLINT_COLOR.THREAD.get();
                    setGlintColor(color != -1 ? color : 0x8040CC, GLINT_ADDITIVE.THREAD.get() ? utmShaders.GLINT_ADDITIVE : utmShaders.GLINT_OVERLAY);
                }
            }
        }
    }
}
