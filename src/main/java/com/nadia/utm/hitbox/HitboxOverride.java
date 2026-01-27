package com.nadia.utm.hitbox;

import com.nadia.utm.utm;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.ScoreHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Objects;

@EventBusSubscriber(modid = "utm")
public class HitboxOverride {
    private static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath("utm", "elytra_speed_reach");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        AttributeInstance reach = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reach == null) return;

        if (player.isFallFlying()) {
            double bonus = player.getDeltaMovement().length() * 1.5;
            utm.LOGGER.info("[UTM] Flight speed: " + player.getDeltaMovement().length());
            reach.removeModifier(RESOURCE);
            
            if (bonus > 0) {
                reach.addTransientModifier(new AttributeModifier(RESOURCE, bonus, AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            if (reach.hasModifier(RESOURCE)) {
                reach.removeModifier(RESOURCE);
            }
        }
    }
}
