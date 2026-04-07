package com.nadia.utm.registry.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record utmItemContainer<I extends Item>(String NAME, DeferredItem<I> ITEM) {
    public static final Map<utmItemContainer<?>, List<String>> DATAGEN_TAGS = new HashMap<>();

    /**
     * automatically generate the item model (basicItem)
     *
     * @return item container
     * @datagen
     */
    public utmItemContainer<I> generated() {
        getForDatagen().add("generated");
        return this;
    }

    /**
     * datagen to music disc shape
     *
     * @return item container
     * @datagen
     */
    public utmItemContainer<I> disc() {
        getForDatagen().add("disc");
        return this;
    }

    public List<String> getForDatagen() {
        return DATAGEN_TAGS.computeIfAbsent(this, k -> new ArrayList<>());
    }
}
