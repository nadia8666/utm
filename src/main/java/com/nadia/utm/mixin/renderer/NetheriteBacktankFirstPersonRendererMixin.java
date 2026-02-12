package com.nadia.utm.mixin.renderer;

import com.nadia.utm.Config;
import com.simibubi.create.content.equipment.armor.NetheriteBacktankFirstPersonRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = NetheriteBacktankFirstPersonRenderer.class, remap = false)
public abstract class NetheriteBacktankFirstPersonRendererMixin {
    @Accessor("rendererActive")
    static boolean utm$getActive() {
        return false;
    }

    @Accessor("BACKTANK_ARMOR_LOCATION")
    static ResourceLocation utm$getArmorLoc() {
        return ResourceLocation.withDefaultNamespace("");
    }

    /**
     * @author nadiarr
     * @reason hate
     */
    @Overwrite
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPlayerHand(RenderArmEvent event) {
        if (!utm$getActive() || !Config.NETHERITE_BACKTANK_ARM.get())
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiBufferSource buffer = event.getMultiBufferSource();
        assert player != null;
        if (!(mc.getEntityRenderDispatcher()
                .getRenderer(player) instanceof PlayerRenderer pr))
            return;

        PlayerModel<AbstractClientPlayer> model = pr.getModel();
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        ModelPart armPart = event.getArm() == HumanoidArm.LEFT ? model.leftSleeve : model.rightSleeve;
        armPart.xRot = 0.0F;

        armPart.render(event.getPoseStack(), buffer.getBuffer(RenderType.entitySolid(utm$getArmorLoc())),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        event.setCanceled(true);
    }
}
