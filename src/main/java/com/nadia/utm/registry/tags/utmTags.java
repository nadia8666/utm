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

        // these are for emi ONLY, not functional
        public static final TagKey<Item> ADD_MENDING = TagKey.create(
                Registries.ITEM,
                utm.key("add_mending"));

        public static final TagKey<Item> ADD_UNBREAKING = TagKey.create(
                Registries.ITEM,
                utm.key("add_unbreaking"));

        public static final TagKey<Item> ADD_SHARPNESS = TagKey.create(
                Registries.ITEM,
                utm.key("add_sharpness"));

        public static final TagKey<Item> ADD_SPEAR_THROW = TagKey.create(
                Registries.ITEM,
                utm.key("add_spear_throw"));

        public static final TagKey<Item> ADD_SPEAR_RECOVERY = TagKey.create(
                Registries.ITEM,
                utm.key("add_spear_recovery"));
        public static final TagKey<Item> ADD_MULTISHOT = TagKey.create(
                Registries.ITEM,
                utm.key("add_multishot"));
        public static final TagKey<Item> ADD_POWER = TagKey.create(
                Registries.ITEM,
                utm.key("add_power"));
        public static final TagKey<Item> ADD_PIERCING = TagKey.create(
                Registries.ITEM,
                utm.key("add_piercing"));
        public static final TagKey<Item> ADD_FLAME = TagKey.create(
                Registries.ITEM,
                utm.key("add_flame"));
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

        public static final TagKey<Block> SOLID_ROCKET_FUEL = TagKey.create(
                Registries.BLOCK,
                utm.key("solid_rocket_fuel"));
    }
}
