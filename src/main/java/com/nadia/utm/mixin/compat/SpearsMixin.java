package com.nadia.utm.mixin.compat;

import com.nadia.utm.hitbox.HitboxOverride;
import com.notunanancyowen.spears.Spears;
import com.notunanancyowen.spears.components.PiercingWeapon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.function.Predicate;

@Mixin(value = PiercingWeapon.class, remap = false)
public class SpearsMixin {
    @Redirect(
            method = "stab",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/notunanancyowen/spears/Spears;collectPiercingCollisions(Lnet/minecraft/world/entity/LivingEntity;FFFLjava/util/function/Predicate;)Ljava/util/Collection;"
            )
    )
    private Collection<EntityHitResult> utm$spearCompat(
            LivingEntity attacker,
            float minReach,
            float maxReach,
            float hitboxMargin,
            Predicate<Entity> filter
    ) {
        if (attacker instanceof LivingEntity living) {
            var reachAttr = living.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);

            if (reachAttr != null) {
                var modifier = reachAttr.getModifier(HitboxOverride.ELYTRA_SPEED_REACH);
                if (modifier != null) {
                    float bonus = (float) modifier.amount();

                    minReach = 0;
                    maxReach += bonus;
                    hitboxMargin = bonus;
                }
            }
        }

        return Spears.collectPiercingCollisions(attacker, minReach, maxReach, hitboxMargin, filter);
    }
}
