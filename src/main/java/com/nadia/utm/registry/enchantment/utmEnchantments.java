package com.nadia.utm.registry.enchantment;

import com.nadia.utm.utm;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class utmEnchantments {
    public static final ResourceKey<Enchantment> POWER_JUMP = ResourceKey.create(
            Registries.ENCHANTMENT,
            utm.key("power_jump")
    );
}
