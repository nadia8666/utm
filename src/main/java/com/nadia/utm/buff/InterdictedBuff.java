package com.nadia.utm.buff;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.rmi.registry.Registry;

public class InterdictedBuff extends MobEffect {
    public InterdictedBuff(MobEffectCategory category, int color) {
        super(category, color);
    }

    public void InterdictionDisable(LivingEntity entity ,ItemStack itemStack) {
        Object serverPlayer = null;

        itemStack.set

        if (itemStack.getItem() instanceof ElytraItem elytraItem ) {

        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(Items.ELYTRA.asItem(),45*20);
            player.getCooldowns().addCooldown(Items.ENDER_PEARL.asItem(),45*20);
            if
            player.getCooldowns().addCooldown(Items.ENDER_PEARL.asItem(),45*20);
        }

        return true;
    }

    // Whether the effect should apply this tick. Used e.g. by the Regeneration effect that only applies
    // once every x ticks, depending on the tick count and amplifier.
    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return tickCount % 40 == 0; // replace this with whatever check you want
    }

    // Utility method that is called when the effect is first added to the entity.
    // This does not get called again until all instances of this effect have been removed from the entity.
    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
    }

    // Utility method that is called when the effect is added to the entity.
    // This gets called every time this effect is added to the entity.
    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
    }
}