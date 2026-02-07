package com.nadia.utm.registry.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class utmItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("utm");

    public static final DeferredItem<Item> UNFINISHED_GLINT_TABLE = ITEMS.registerSimpleItem("unfinished_glint_table");

}
