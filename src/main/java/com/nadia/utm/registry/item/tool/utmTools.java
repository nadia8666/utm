package com.nadia.utm.registry.item.tool;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.item.FiddleheadItem;
import com.nadia.utm.item.NetherytraItem;
import com.nadia.utm.registry.item.utmItemContainer;
import com.nadia.utm.registry.tags.utmTags;
import com.nadia.utm.registry.utmRegistry;
import com.nadia.utm.tool.CopperSword;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@ForceLoad(deps = {utmRegistry.class, utmTags.class})
public class utmTools {
    public static final DeferredRegister.Items TOOLS = utmRegistry.ITEMS;

    public static <I extends Item> utmItemContainer<I> register(String name, Supplier<I> func) {
        DeferredItem<I> item = TOOLS.register(name, func);
        return new utmItemContainer<>(name, item);
    }

    public static utmItemContainer<Item> register(String name, Item.Properties properties) {
        DeferredItem<Item> item = TOOLS.registerItem(name, Item::new, properties);
        return new utmItemContainer<>(name, item);
    }

    public static utmItemContainer<Item> register(String name) {
        DeferredItem<Item> item = TOOLS.registerItem(name, Item::new, new Item.Properties());
        return new utmItemContainer<>(name, item);
    }

    public static final Tier TIER_COPPER = utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 0, 7f, 2, 67,
            () -> Ingredient.of(Tags.Items.INGOTS_COPPER));

    public static final utmItemContainer<CopperSword> COPPER_SWORD = register("copper_sword", () -> new CopperSword(TIER_COPPER,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_COPPER, 5, -2.4f)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<ShovelItem> COPPER_SHOVEL = register("copper_shovel", () -> new ShovelItem(TIER_COPPER,
            new Item.Properties().attributes(
                    ShovelItem.createAttributes(TIER_COPPER, 1.5f, -3)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SHOVELS).handheld();

    public static final utmItemContainer<AxeItem> COPPER_AXE = register("copper_axe", () -> new AxeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    AxeItem.createAttributes(TIER_COPPER, 6.111f, -3.98f)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.AXES).handheld();

    public static final utmItemContainer<PickaxeItem> COPPER_PICKAXE = register("copper_pickaxe", () -> new PickaxeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    PickaxeItem.createAttributes(TIER_COPPER, 1, -1)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.PICKAXES).handheld();

    public static final utmItemContainer<HoeItem> COPPER_HOE = register("copper_hoe", () -> new HoeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    HoeItem.createAttributes(TIER_COPPER, 0, -1)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.HOES).handheld();

    public static final utmItemContainer<FiddleheadItem> FIDDLEHEAD = register("fiddlehead", () -> new FiddleheadItem(new Item.Properties().durability(72000)));

    public static final utmItemContainer<NetherytraItem> NETHERYTRA = register("netherytra", () -> new NetherytraItem(new Item.Properties().durability(850).fireResistant()))
            .tags(Tags.Items.ENCHANTABLES, ItemTags.EQUIPPABLE_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE).generated();

    public static final utmItemContainer<SwordItem> OBSIDIAN_SWORD = register("obsidian_sword", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4200, 1f, 0f, 5, () -> Ingredient.of(Tags.Items.OBSIDIANS)
                );
                return new SwordItem(Tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(Tier, 12, -3.75f)));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    private static final Tier TIER_ENCHANTED_SWORD = utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 8200, 1f, 0f, 25, () -> Ingredient.EMPTY
    );

    public static final utmItemContainer<SwordItem> ENCHANTED_SWORD_RED = register("enchanted_sword_red", () -> new SwordItem(TIER_ENCHANTED_SWORD,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_ENCHANTED_SWORD, 5, -2.4f)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<SwordItem> ENCHANTED_SWORD_GREEN = register("enchanted_sword_green", () -> new SwordItem(TIER_ENCHANTED_SWORD,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_ENCHANTED_SWORD, 5, -2.4f)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<SwordItem> ENCHANTED_SWORD_BLUE = register("enchanted_sword_blue", () -> new SwordItem(TIER_ENCHANTED_SWORD,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_ENCHANTED_SWORD, 5, -2.4f)
            ))
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<SwordItem> CYCLESWORD = register("cyclesword", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 350, 1f, 0f, 15, () -> Ingredient.of(Tags.Items.INGOTS_IRON)
                );
                return new SwordItem(Tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(Tier, 4, -1.125f)));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<AxeItem> ARID_AXE = register("arid_axe", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 2500, 8f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new AxeItem(Tier, new Item.Properties().attributes(
                        AxeItem.createAttributes(Tier, 17, -3.4f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.AXES).handheld();

    public static final utmItemContainer<SwordItem> ARID_SWORD = register("arid_sword", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 5000, 8f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new SwordItem(Tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(Tier, 6.5f, 6)));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<SwordItem> SHART = register("shart", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 500, 1f, 0f, 15, () -> Ingredient.of(Tags.Items.INGOTS_IRON)
                );
                return new SwordItem(Tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(Tier, 5, -2.4f)));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();
}