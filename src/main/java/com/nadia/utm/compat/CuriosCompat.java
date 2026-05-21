package com.nadia.utm.compat;

import com.nadia.utm.utm;
import com.simibubi.create.AllItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@EventBusSubscriber(modid = "utm")
public class CuriosCompat {
    @SubscribeEvent
    public static void onCurioAttribute(CurioAttributeModifierEvent event) {
        Item item = event.getItemStack().getItem();

        if (item == AllItems.NETHERITE_BACKTANK.get()) {
            applyNetheriteStats(event);
        } else if (item == AllItems.COPPER_BACKTANK.get()) {
            event.addModifier(Attributes.ARMOR, new AttributeModifier(
                    utm.key("copper_backtank"),
                    4.0,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    private static void applyNetheriteStats(CurioAttributeModifierEvent event) {
        event.addModifier(Attributes.ARMOR, new AttributeModifier(
                utm.key("netherite_backtank_arm"),
                8.0,
                AttributeModifier.Operation.ADD_VALUE
        ));
        event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                utm.key("netherite_backtank_tough"),
                3.0,
                AttributeModifier.Operation.ADD_VALUE
        ));
        event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(
                utm.key("netherite_backtank_res"),
                0.1,
                AttributeModifier.Operation.ADD_VALUE
        ));
    }
}