package com.nadia.utm.mixin.compat.create;

import com.nadia.utm.item.AdvancedGogglesItem;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GoggleOverlayRenderer.class, remap = false)
public class GoggleOverlayRendererMixin {
    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private static void utm$cancelForAdvanced(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || AdvancedGogglesItem.isWearingAdvancedGoggles(mc.player)) ci.cancel();
    }
}
