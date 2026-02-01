package com.nadia.utm.mixin;


import com.nadia.utm.behaviour.EanFlightBehaviour;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;


@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level){
        super(entityType,level);
    }
    @ModifyArg(
            method = "travel",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;checkSlowFallDistance()V")
            ),
            at= @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )    private Vec3 ean_modifyVelocity(Vec3 vector){
        Vec3 eanFlightVector = EanFlightBehaviour.ean_flightBehaviour(((LivingEntity)(Object)this));
        if (eanFlightVector != null)
            return eanFlightVector;
        else
            return vector;
    }
}
