package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Gui.class, remap = true)
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


     @Definition(id = "crosshairPickEntity", field = "Lnet/minecraft/client/Minecraft;crosshairPickEntity:Lnet/minecraft/world/entity/Entity;")
      @Definition(id = "minecraft", field = "Lnet/minecraft/client/gui/Gui;minecraft:Lnet/minecraft/client/Minecraft;")
      @Definition(id = "player", field = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/player/LocalPlayer;")
      @Definition(id = "LivingEntity", type = LivingEntity.class)
     @Definition(id = "f", local = @Local(type = float.class))
     @Expression("f < 1.0")
      @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
      private boolean condition(boolean original) {
        // still not what i wanted so i am going to kill myself
         return original || (this.minecraft.player.getInventory().getSelected().is(utmTools.SWORD_OF_KIRK.ITEM()));
     }
}
