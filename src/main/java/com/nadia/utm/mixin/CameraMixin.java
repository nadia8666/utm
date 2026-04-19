package com.nadia.utm.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(value = Camera.class, priority = 2000, remap = false)
public abstract class CameraMixin {
    @TargetHandler(
            mixin = "org.figuramc.figura.mixin.render.CameraMixin",
            name = "setupRot"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void utm$cancelFiguraSetupRot(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo originalCi, CallbackInfo ci) {
        ci.cancel();
    }
}