package com.nadia.utm.registry.item.tool;

import com.nadia.utm.item.FiddleheadItem;
import com.nadia.utm.item.NetherytraItem;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.tool.CopperSword;
import com.nadia.utm.utm;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Supplier;

public class utmTools {
    // tool set
    public static final Tier TIER_COPPER = utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 0, 7f, 2, 67,
            () -> Ingredient.of(Tags.Items.INGOTS_COPPER));

    public static final Supplier<CopperSword> COPPER_SWORD = utmItems.ITEMS.register("copper_sword", () -> new CopperSword(TIER_COPPER,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_COPPER, 5, -2.4f)
            ))
    );
    public static final Supplier<ShovelItem> COPPER_SHOVEL = utmItems.ITEMS.register("copper_shovel", () -> new ShovelItem(TIER_COPPER,
            new Item.Properties().attributes(
                    ShovelItem.createAttributes(TIER_COPPER, 1.5f, -3)
            ))
    );
    public static final Supplier<AxeItem> COPPER_AXE = utmItems.ITEMS.register("copper_axe", () -> new AxeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    AxeItem.createAttributes(TIER_COPPER, 6.111f, -3.98f)
            ))
    );
    public static final Supplier<PickaxeItem> COPPER_PICKAXE = utmItems.ITEMS.register("copper_pickaxe", () -> new PickaxeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    PickaxeItem.createAttributes(TIER_COPPER, 1, -1)
            ))
    );
    public static final Supplier<HoeItem> COPPER_HOE = utmItems.ITEMS.register("copper_hoe", () -> new HoeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    HoeItem.createAttributes(TIER_COPPER, 0, -1)
            ))
    );
    public static final Supplier<FiddleheadItem> FIDDLEHEAD = utmItems.ITEMS.register("fiddlehead", () -> new FiddleheadItem(new Item.Properties().durability(72000)));
    public static final Supplier<NetherytraItem> NETHERYTRA = utmItems.ITEMS.register("netherytra", () -> new NetherytraItem(new Item.Properties().durability(850).fireResistant()));

    public static final Supplier<SwordItem> OBSIDIAN_SWORD = utmItems.ITEMS.register("obsidian_sword", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4200, 1f, 0f, 5, () -> Ingredient.of(Tags.Items.OBSIDIANS)
                );
                return new SwordItem(Tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(Tier, 12, -3.75f)));
            }
    );
    private static final Tier TIER_ENCHANTED_SWORD = utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 8200, 1f, 0f, 25, () -> Ingredient.EMPTY
    );
    public static final Supplier<SwordItem> ENCHANTED_SWORD_RED = utmItems.ITEMS.register("enchanted_sword_red", () -> new SwordItem(TIER_ENCHANTED_SWORD,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_ENCHANTED_SWORD, 5, -2.4f)
            ))
    );
    public static final Supplier<SwordItem> ENCHANTED_SWORD_GREEN = utmItems.ITEMS.register("enchanted_sword_green", () -> new SwordItem(TIER_ENCHANTED_SWORD,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_ENCHANTED_SWORD, 5, -2.4f)
            ))
    );
    public static final Supplier<SwordItem> ENCHANTED_SWORD_BLUE = utmItems.ITEMS.register("enchanted_sword_blue", () -> new SwordItem(TIER_ENCHANTED_SWORD,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_ENCHANTED_SWORD, 5, -2.4f)
            ))
    );
    public static final Supplier<SwordItem> CYCLESWORD = utmItems.ITEMS.register("cyclesword", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 350, 1f, 0f, 15, () -> Ingredient.of(Tags.Items.INGOTS_IRON)
                );
                return new SwordItem(Tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(Tier, 4, -1.125f)));
            }
    );
    public static final Supplier<AxeItem> ARID_AXE = utmItems.ITEMS.register("arid_axe", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 2500, 8f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new AxeItem(Tier, new Item.Properties().attributes(
                        AxeItem.createAttributes(Tier, 17, -3.4f)).rarity(Rarity.RARE));
            }
    );
    public static final Supplier<SwordItem> ARID_SWORD = utmItems.ITEMS.register("arid_sword", () -> {
                var Tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 5000, 8f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new SwordItem(Tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(Tier, 6.5f, 6)));
            }
    );
    public static void doNothing() {
        utm.LOGGER.info("[UTM] Tools class loaded, so that it shows up!");
    }
}
