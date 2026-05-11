package com.nadia.utm.mixin;

import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, remap = false)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @Inject(method = "shouldRenderAtSqrDistance", at = @At("RETURN"), cancellable = true)
    private void utm$forceRenderSqr(double distance, CallbackInfoReturnable<Boolean> cir) {
        if (level().dimension().equals(utmDimensions.SPACE_KEY))
            cir.setReturnValue(true);
    }

    @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true)
    private void utm$forceRender(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (level().dimension().equals(utmDimensions.SPACE_KEY))
            cir.setReturnValue(true);
    }
}
