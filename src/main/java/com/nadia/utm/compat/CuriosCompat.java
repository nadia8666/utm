package com.nadia.utm.compat;

import com.simibubi.create.AllItems;
import net.minecraft.resources.ResourceLocation;
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
                    ResourceLocation.fromNamespaceAndPath("utm", "copper_backtank"),
                    4.0,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    private static void applyNetheriteStats(CurioAttributeModifierEvent event) {
        event.addModifier(Attributes.ARMOR, new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("utm", "netherite_backtank_arm"),
                8.0,
                AttributeModifier.Operation.ADD_VALUE
        ));
        event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("utm", "netherite_backtank_tough"),
                3.0,
                AttributeModifier.Operation.ADD_VALUE
        ));
        event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("utm", "netherite_backtank_res"),
                0.1,
                AttributeModifier.Operation.ADD_VALUE
        ));
    }
}