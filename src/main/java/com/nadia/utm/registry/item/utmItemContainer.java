package com.nadia.utm.registry.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class utmItemContainer<I extends Item> {
    public static final List<utmItemContainer<?>> ALL_ITEMS = new ArrayList<>();
    public static final Map<utmItemContainer<?>, List<String>> DATAGEN_TARGETS = new HashMap<>();
    public final List<TagKey<Item>> DATAGEN_TAGS = new ArrayList<>();
    public final String NAME;
    public final DeferredItem<I> ITEM;

    public utmItemContainer(String name, DeferredItem<I> item) {
        NAME = name;
        ITEM = item;

        ALL_ITEMS.add(this);
    }

    public String NAME() {
        return NAME;
    }

    public DeferredItem<I> ITEM() {
        return ITEM;
    }

    public I get() {
        return ITEM.get();
    }

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
     * automatically generate the item model (handheldItem)
     *
     * @return item container
     * @datagen
     */
    public utmItemContainer<I> handheld() {
        getForDatagen().add("handheld");
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

    /**
     * add tags
     *
     * @param tags tags
     * @datagen
     */
    @SafeVarargs
    public final utmItemContainer<I> tags(TagKey<Item>... tags) {
        this.DATAGEN_TAGS.addAll(List.of(tags));
        return this;
    }

    public List<String> getForDatagen() {
        return DATAGEN_TARGETS.computeIfAbsent(this, k -> new ArrayList<>());
    }
}
