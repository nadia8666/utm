package com.nadia.utm.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnchantUtil {
    @SafeVarargs
    public static boolean findEnchants(@NotNull ItemStack stack, @NotNull ItemStack book, ResourceKey<Enchantment>... toFind) {
        Set<ResourceKey<Enchantment>> search = Arrays.stream(toFind).collect(Collectors.toSet());

        ItemEnchantments enchants = book.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchants != null)
            for (Holder<Enchantment> enchant : enchants.keySet())
                if (search.contains(enchant.getKey())) return true;

        return false;
    }
}
