package com.nadia.utm.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
public class GreenlineDPickaxeItem extends PickaxeItem {
    public GreenlineDPickaxeItem(Tier p_42961_, Properties p_42964_) {
        super(p_42961_, p_42964_);
    }



    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (Math.abs(entity.getLookAngle().y()) < 0.1f) {
            //THis picakxe is meant to mine faster if youre looking in one specific direction but i have no clue how Tocode that. so : ) hello!

        }
    }
}
