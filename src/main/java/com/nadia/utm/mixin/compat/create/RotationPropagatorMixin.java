package com.nadia.utm.mixin.compat.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.registry.tags.utmTags;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RotationPropagator.class, remap = false)
public class RotationPropagatorMixin {
    @ModifyVariable(
            method = "propagateNewSource",
            at = @At("STORE"),
            name = "tooFast"
    )
    private static boolean utm$OverrideTooFast(boolean tooFast, @Local(name = "currentTE") KineticBlockEntity currentTE, @Local(name = "neighbourTE") KineticBlockEntity neighbourTE) {
        if (currentTE.getBlockState().is(utmTags.BLOCK.BYPASS_RPM))
            return false;

        return tooFast;
    }
}
