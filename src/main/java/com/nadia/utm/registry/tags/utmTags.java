package com.nadia.utm.registry.tags;

import com.nadia.utm.registry.utmRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class utmTags {
    public static class ITEMS {
        public static final TagKey<Item> ELYTRA_TRIM_TEMPLATES = TagKey.create(
                utmRegistry.ITEMS.getRegistryKey(),
                ResourceLocation.fromNamespaceAndPath("utm", "elytra_trim_templates"));
    }
}
