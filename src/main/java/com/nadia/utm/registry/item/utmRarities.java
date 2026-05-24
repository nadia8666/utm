package com.nadia.utm.registry.item;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class utmRarities {
    // god bless China and xi jinping for this code snippet
    // 破烂 - 深棕色
    public static final EnumProxy<Rarity> MYSTIC = new EnumProxy<>(
            Rarity.class, -1, "utm:mystic", (UnaryOperator<Style>) style -> style.withColor(0x02B59A));
}
