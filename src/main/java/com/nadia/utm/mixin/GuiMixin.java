package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.sound.utmSounds;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.checkerframework.checker.signature.qual.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin(value = Gui.class, remap = false)
public class GuiMixin {

    private Minecraft minecraft;

    // I learned while making this...
    //and it doesnt work anyways. So. Who cares.

    /*
    @Definition(id = "crosshairPickEntity", field = "Lnet/minecraft/client/Minecraft;crosshairPickEntity:Lnet/minecraft/world/entity/Entity;")
    @Definition(id = "minecraft", field = "Lnet/minecraft/client/gui/Gui;minecraft:Lnet/minecraft/client/Minecraft;")
    @Definition(id = "LivingEntity", type = LivingEntity.class)
    @Definition(id = "f", method = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F")
    @Expression({"this.minecraft.crosshairPickEntity != null", "this.minecraft.crosshairPickEntity instanceof LivingEntity", "f >= 1.0"})
    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean condition(boolean original) {

        return original || (this.minecraft.player.getInventory().getSelected().is(utmTools.SWORD2.ITEM()));
   }
    */

 //                    if ((this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) || this.minecraft.player.getInventory().getSelected().is(utmTools.SWORD2.ITEM())) {
 //Big Mambo rewrite this code so its not poop and stupid

/*
     @Definition(id = "f", local = @Local(type = float.class))
     @Expression("f < 1.0")
      @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
      private boolean condition(boolean original) {
        // still not what i wanted so i am going to kill myself
         return original || (this.minecraft.player.getInventory().getSelected().is(utmTools.SWORD_OF_KIRK.ITEM()));
     }

*/

     //SLOW, MAYBE TANKS FRAMES.. SLOW!!
    @Unique boolean care = false;
    @Unique private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = ResourceLocation.parse("hud/crosshair_attack_indicator_full");
    @Inject(method = "renderCrosshair", at = @At(value = "TAIL"))
    private void enhancedAttackIndicator$showPlus(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        float f = this.minecraft.player.getAttackStrengthScale(0.0F);
     //   Logger.getLogger("utm").warning(String.valueOf(f));
        if (this.minecraft.crosshairPickEntity == null && this.minecraft.player.getInventory().getSelected().is(utmTools.SWORD_OF_KIRK.ITEM()) && f>=1f) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int j = guiGraphics.guiHeight() / 2 - 7 + 16;
            int k = guiGraphics.guiWidth() / 2 - 8;
            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, k, j, 16, 16);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            if (care==false) {
                care=true;

                Minecraft.getInstance().getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.CROSSBOW_LOADING_END.value(), 1f, .8f)
                );
            }
        } else if (f<1) {
            care=false;
        }
    }

}
