package com.nadia.utm.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;

public class WritingSwordItem extends SwordItem {
    public WritingSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }
    // i dont have an ame for this item yet : D So its called the writing sword forever now

    public WritingSwordItem(Tier p_tier, Properties p_properties, Tool toolComponentData) {
        super(p_tier, p_properties, toolComponentData);
    }
}
