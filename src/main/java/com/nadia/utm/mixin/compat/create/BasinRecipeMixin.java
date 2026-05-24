package com.nadia.utm.mixin.compat.create;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {
    @Inject(method = "getMaxFluidInputCount", at = @At("RETURN"), cancellable = true)
    private void utm$awesome(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(10);
    }
}
