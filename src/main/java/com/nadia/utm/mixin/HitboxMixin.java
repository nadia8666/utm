package com.nadia.utm.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class HitboxMixin {
    // tl;dr, hook into getEntityHitResult's entity1.getPickRadius() call, and return a changed value based on origin speed
    @Redirect(
            method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBox()Lnet/minecraft/world/phys/AABB;"),
            remap = false
    )
    private static AABB utm$expandHitbox(
            Entity target,
            Entity shooter, Vec3 start, Vec3 end, AABB box,
            Predicate<Entity> filter, double dist
    ) {
        AABB originalBox = target.getBoundingBox();

        if (shooter instanceof Player player && player.isFallFlying()) {
            double speed = player.getDeltaMovement().horizontalDistance();
            double expansion = speed * 1.5;

            return originalBox.inflate(expansion);
        }

        return originalBox;
    }
}