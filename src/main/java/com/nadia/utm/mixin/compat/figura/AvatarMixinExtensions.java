package com.nadia.utm.mixin.compat.figura;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nadia.utm.client.renderer.IAvatarRendererExtensions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.model.rendering.AvatarRenderer;
import org.figuramc.figura.model.rendering.PartFilterScheme;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Avatar.class, remap = false)
public abstract class AvatarMixinExtensions implements IAvatarRendererExtensions {
    @Shadow
    public boolean loaded;
    @Shadow
    public AvatarRenderer renderer;

    @Shadow
    public abstract void render(Entity entity, float yaw, float delta, float alpha, PoseStack stack, MultiBufferSource bufferSource, int light, int overlay, LivingEntityRenderer<?, ?> entityRenderer, PartFilterScheme filter, boolean translucent, boolean glowing);

    @Unique
    public void utm$firstPersonRender(PoseStack stack, MultiBufferSource bufferSource, Player player, PlayerRenderer playerRenderer, ModelPart arm, int light, float tickDelta, boolean lefty) {
        if (this.renderer != null && this.loaded) {
            FiguraMod.pushProfiler("figura");
            FiguraMod.pushProfiler((Avatar) (Object) this);
            FiguraMod.pushProfiler("firstPersonRender");
            FiguraMod.pushProfiler(lefty ? "leftArm" : "rightArm");

            PartFilterScheme filter = lefty ? PartFilterScheme.LEFT_ARM : PartFilterScheme.RIGHT_ARM;
            this.renderer.allowHiddenTransforms = false;
            this.renderer.allowMatrixUpdate = false;
            this.renderer.ignoreVanillaVisibility = true;

            stack.pushPose();
            stack.mulPose(Axis.ZP.rotation(arm.zRot));
            stack.mulPose(Axis.YP.rotation(arm.yRot));
            stack.mulPose(Axis.XP.rotation(arm.xRot));

            this.render(player, 0.0F, tickDelta, 1.0F, stack, bufferSource, light, OverlayTexture.NO_OVERLAY, playerRenderer, filter, false, false);

            stack.popPose();
            this.renderer.allowHiddenTransforms = true;
            this.renderer.ignoreVanillaVisibility = false;
            FiguraMod.popProfiler(4);
        }
    }


    /**
     * @author nadiarr
     * @reason punchy left arm
     */
    @Overwrite
    public void firstPersonRender(PoseStack stack, MultiBufferSource bufferSource, Player player, PlayerRenderer playerRenderer, ModelPart arm, int light, float tickDelta) {
        boolean lefty = arm == playerRenderer.getModel().leftArm;
        this.utm$firstPersonRender(stack, bufferSource, player, playerRenderer, arm, light, tickDelta, lefty);
    }
}
