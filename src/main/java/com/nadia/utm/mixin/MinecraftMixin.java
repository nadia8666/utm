package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public HitResult hitResult;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(
            method = "startAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/event/InputEvent$InteractionKeyMappingTriggered;isCanceled()Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void utm$postClick(CallbackInfoReturnable<Boolean> cir, @Local InputEvent.InteractionKeyMappingTriggered inputEvent, @Local ItemStack itemStack) {
        if (!inputEvent.isCanceled() && hitResult != null && (hitResult.getType().equals(HitResult.Type.BLOCK) || hitResult.getType().equals(HitResult.Type.MISS))) {
            if (false) {
                if (inputEvent.shouldSwingHand())
                    player.swing(InteractionHand.MAIN_HAND);

                cir.setReturnValue(false);
            }
        }
    }
}
