package com.nadia.utm.hitbox;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class HitboxOverride {
    public static final ResourceLocation ELYTRA_SPEED_REACH = ResourceLocation.fromNamespaceAndPath("utm", "elytra_speed_reach");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        AttributeInstance reach = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reach == null) return;

        if (player.isFallFlying()) {
            double bonus = player.getDeltaMovement().length() * 1.5;
            reach.removeModifier(ELYTRA_SPEED_REACH);
            
            if (bonus > 0) {
                reach.addTransientModifier(new AttributeModifier(ELYTRA_SPEED_REACH, bonus, AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            if (reach.hasModifier(ELYTRA_SPEED_REACH)) {
                reach.removeModifier(ELYTRA_SPEED_REACH);
            }
        }
    }
}
