package com.nadia.utm.registry.tags;

import com.nadia.utm.utm;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class utmTags {
    public static class ITEM {
        public static final TagKey<Item> ELYTRA_TRIM_TEMPLATES = TagKey.create(
                Registries.ITEM,
                utm.key("elytra_trim_templates"));
    }

    public static class BLOCK {
        public static final TagKey<Block> A23_ORE_REPLACEABLE = TagKey.create(
                Registries.BLOCK,
                utm.key("a23_ore_replaceable"));

        public static final TagKey<Block> SEAL_NOPROP = TagKey.create(
                Registries.BLOCK,
                utm.key("seal_noprop"));

        public static final TagKey<Block> UNSEALED = TagKey.create(
                Registries.BLOCK,
                utm.key("unsealed"));

        public static final TagKey<Block> SEALED = TagKey.create(
                Registries.BLOCK,
                utm.key("sealed"));

        public static final TagKey<Block> BYPASS_RPM = TagKey.create(
                Registries.BLOCK,
                utm.key("bypass_max_rpm"));
    }
}
