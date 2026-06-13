package com.nadia.utm.item;

import com.nadia.utm.util.EnchantUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class AridSlingshotItem extends Item {
    public AridSlingshotItem(Properties properties) {
        super(properties);
    }




    @Override
    public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
        if (EnchantUtil.findEnchants(stack, book, Enchantments.MENDING))
            return false;

        return super.isBookEnchantable(stack, book);
    }
}
