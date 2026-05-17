package com.nadia.utm.mixin;

import com.nadia.utm.registry.planets.utmPlanets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemEntity.class, remap = false)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), cancellable = true)
    protected void utm$spaceGravity(CallbackInfoReturnable<Double> cir) {
        if (utmPlanets.get(this) instanceof utmPlanets.Planet planet)
            cir.setReturnValue(Math.max(0.04 * (planet.GRAVITY()/.08), 0));
    }
}
