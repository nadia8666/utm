package com.nadia.utm.tool;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class Paxel extends DiggerItem {
    //dont call them Paxels bro! nadia renames this to paxel item bcz he is a bum
    public Paxel(Tier p_42961_, Item.Properties p_42964_) {
        super(p_42961_, (BlockTags.MINEABLE_WITH_PICKAXE), p_42964_);
    }
    // i DOnt know how to do this
    // SO BASICALLY it will jujst le epically stay in this state until i straight up Know how to make a The pixels
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility) || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility) || ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
    }
}
