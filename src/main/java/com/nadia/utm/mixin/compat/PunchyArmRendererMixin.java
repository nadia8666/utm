package com.nadia.utm.mixin.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nadia.utm.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import punchy.client.render.PunchyArmRenderer;
import punchy.config.PunchyConfig;

@Mixin(value = PunchyArmRenderer.class, remap = false)
public abstract class PunchyArmRendererMixin {
    @Shadow
    private static ModelPart cloneModelPart(ModelPart source) {
        return null;
    }

    @Shadow
    private static void applyArmMeshOffsets(PoseStack poseStack, boolean leftArm) {
    }

    @Shadow
    private static void applyFreezeShake(PoseStack poseStack, AbstractClientPlayer player, float partialTicks) {
    }

    @Shadow
    private static void copyMatrixToSleeve(ModelPart arm, ModelPart sleeve) {
    }

    @Shadow
    private static void renderLavaHandOverlay(ModelPart armPart, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, HumanoidArm arm, float partialTicks) {
    }

    @Shadow
    private static void renderFreezeOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, float partialTicks) {
    }

    @Shadow
    private static void renderMudOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
    }

    @Shadow
    private static void renderSweatOverlay(ModelPart armPart, boolean isLeft, boolean slim, PoseStack poseStack, MultiBufferSource buffer, int combinedLight) {
    }

    @Shadow
    private static void renderFlameOnArm(PoseStack poseStack, MultiBufferSource buffer) {
    }

    /**
     * @author nadiarr
     * @reason punchy uses regular arm models we ove rhere use figura
     */
    @Overwrite
    private static void renderArm(PlayerModel<?> playerModel, AbstractClientPlayer player, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, ResourceLocation texture, boolean slim, float partialTicks) {
        ModelPart armPart = cloneModelPart(arm == HumanoidArm.LEFT ? playerModel.leftArm : playerModel.rightArm);
        ModelPart sleevePart = cloneModelPart(arm == HumanoidArm.LEFT ? playerModel.leftSleeve : playerModel.rightSleeve);
        if (armPart != null && sleevePart != null) {
            boolean isLeft = arm == HumanoidArm.LEFT;
            poseStack.pushPose();
            if (slim) {
                poseStack.translate((isLeft ? 1.0F : -1.0F) * 0.5F / 16.0F, 0.0F, 0.0F);
            }

            applyArmMeshOffsets(poseStack, isLeft);
            applyFreezeShake(poseStack, player, partialTicks);
            copyMatrixToSleeve(armPart, sleevePart);

            boolean figura = false;
            if (Config.FIGURA_PUNCHY.getAsBoolean()) {
                Avatar avatar = AvatarManager.getAvatar(player);
                EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
                if (renderer instanceof PlayerRenderer pRenderer) {
                    if (avatar != null) {
                        poseStack.pushPose();

                        armPart.translateAndRotate(poseStack);
                        poseStack.translate((isLeft ? 8F : 5F) / 16F, -2F / 16F, 0);

                        avatar.firstPersonRender(poseStack, buffer, player, pRenderer, armPart, combinedLight, partialTicks);
                        figura = true;

                        poseStack.popPose();
                    }
                }
            }

            if (!figura) {
                VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
                armPart.render(poseStack, consumer, combinedLight, OverlayTexture.NO_OVERLAY);
                if (sleevePart.visible) {
                    sleevePart.render(poseStack, consumer, combinedLight, OverlayTexture.NO_OVERLAY);
                }
            }

            renderLavaHandOverlay(armPart, poseStack, buffer, combinedLight, player, isLeft ? HumanoidArm.LEFT : HumanoidArm.RIGHT, partialTicks);
            renderFreezeOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight, player, partialTicks);
            renderMudOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight);
            renderSweatOverlay(armPart, isLeft, slim, poseStack, buffer, combinedLight);
            if (player.isOnFire()) {
                poseStack.pushPose();
                armPart.translateAndRotate(poseStack);
                if (!PunchyConfig.disableEnhancedFireArmEffects()) {
                    renderFlameOnArm(poseStack, buffer);
                }

                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }
}
