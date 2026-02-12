package com.nadia.utm.mixin.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.client.renderer.utmShaders;
import com.simibubi.create.content.equipment.armor.BacktankArmorLayer;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.*;
import static com.nadia.utm.client.renderer.glint.utmGlintContainer.DEFAULT_COLOR;

@Mixin(value = BacktankArmorLayer.class, remap = false)
public class BacktankArmorLayerMixin<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public BacktankArmorLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    /**
     * @author nadiarr
     * @reason so. create 6.0.9 doesn't have this changed becuase it was done the day after and im pissed
     */
    @Overwrite
    public void render(@NotNull PoseStack ms, @NotNull MultiBufferSource buffer, int light, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity.getPose() == Pose.SLEEPING)
            return;

        BacktankItem item = BacktankItem.getWornBy(entity);
        if (item == null)
            return;

        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel<?> model))
            return;

        var stack = entity.getItemBySlot(BacktankItem.SLOT);
        if (buffer instanceof MultiBufferSource.BufferSource source) {
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

        boolean hasGlint = stack.hasFoil();
        VertexConsumer vc = hasGlint ? VertexMultiConsumer.create(buffer.getBuffer(GLINT_ADDITIVE.THREAD.get() ? utmRenderTypes.ADDITIVE_GLINT_ITEM.get() : utmRenderTypes.OVERLAY_GLINT_ITEM.get()
        ), buffer.getBuffer(Sheets.cutoutBlockSheet())) : buffer.getBuffer(Sheets.cutoutBlockSheet());
        BlockState renderedState = item.getBlock().defaultBlockState()
                .setValue(BacktankBlock.HORIZONTAL_FACING, Direction.SOUTH);
        SuperByteBuffer backtank = CachedBuffers.block(renderedState);
        SuperByteBuffer cogs = CachedBuffers.partial(BacktankRenderer.getCogsModel(renderedState), renderedState);
        SuperByteBuffer nob = CachedBuffers.partial(BacktankRenderer.getShaftModel(renderedState), renderedState);

        ms.pushPose();

        model.body.translateAndRotate(ms);
        ms.translate(-1 / 2f, 10 / 16f, 1f);
        ms.scale(1, -1, -1);

        backtank.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        nob.disableDiffuse()
                .translate(0, -3f / 16, 0)
                .light(light)
                .renderInto(ms, vc);

        cogs.center()
                .rotateYDegrees(180)
                .uncenter()
                .translate(0, 6.5f / 16, 11f / 16)
                .rotate(AngleHelper.rad(2 * AnimationTickHolder.getRenderTime(entity.level()) % 360), Direction.EAST)
                .translate(0, -6.5f / 16, -11f / 16);

        cogs.disableDiffuse()
                .light(light)
                .renderInto(ms, vc);

        ms.popPose();
    }
}
