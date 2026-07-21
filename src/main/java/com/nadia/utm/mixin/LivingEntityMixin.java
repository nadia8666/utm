package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingEntity.class, remap = false)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    //This is a foundational mixin which i think changes literally evyerhting about iframes since its from such a big mode
    //So its affects must be studied please study them since i ltegit dont know what this does

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/damagesource/DamageContainer;getPostAttackInvulnerabilityTicks()I"))
    public int changeIFrames(DamageContainer instance, @Local(ordinal = 0, argsOnly = true) final DamageSource source) {

        Entity entity2 = source.getDirectEntity();
        int invulnerableTime = instance.getPostAttackInvulnerabilityTicks();
        if (source.is(DamageTypes.PLAYER_ATTACK) && entity2 instanceof Player player) {

            boolean canHit = false;

            ItemAttributeModifiers mods = player.getMainHandItem().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            for (ItemAttributeModifiers.Entry entry : mods.modifiers()) {
                if (entry.attribute() == Attributes.ATTACK_DAMAGE) {
                    canHit = true;
                    //what does htis do?
                }
            }

            canHit=true;

            if (canHit) {
                int yeah = 10;
                ItemStack stack = player.getMainHandItem();

                if (stack.is(utmTools.ARID_SWORD.get())) yeah=5;
                else if(stack.is(utmTools.GLOVE.get())) yeah=1;

                invulnerableTime = yeah;

            }
        }
        else if (source.is(DamageTypeTags.IS_FALL)) {
            invulnerableTime = 10;
        }
        else if (source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
            invulnerableTime = 0;
        } else {
            invulnerableTime=10;
        }
        return invulnerableTime;
    }


    @ModifyExpressionValue(method = "hurt", at = @At(value = "CONSTANT", args = "floatValue=10.0f", ordinal = 0))
    public float changeIFrames(float constant) {
        return constant - 10;
    }
}
