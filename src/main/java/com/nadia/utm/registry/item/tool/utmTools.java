package com.nadia.utm.registry.item.tool;

import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.utm;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Supplier;

public class utmTools {
    // tool set
    public static final Tier TIER_COPPER = utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_GOLD_TOOL, 80, 5f, 2, 20,
            () -> Ingredient.of(Tags.Items.INGOTS_COPPER));

    public static final Supplier<SwordItem> COPPER_SWORD = utmItems.ITEMS.register("copper_sword", () -> new SwordItem(TIER_COPPER,
            new Item.Properties().attributes(
                    SwordItem.createAttributes(TIER_COPPER, 4, -2)
            ))
    );
    public static final Supplier<ShovelItem> COPPER_SHOVEL = utmItems.ITEMS.register("copper_shovel", () -> new ShovelItem(TIER_COPPER,
            new Item.Properties().attributes(
                    ShovelItem.createAttributes(TIER_COPPER, 4, -2)
            ))
    );
    public static final Supplier<AxeItem> COPPER_AXE = utmItems.ITEMS.register("copper_axe", () -> new AxeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    AxeItem.createAttributes(TIER_COPPER, 4, -2)
            ))
    );
    public static final Supplier<PickaxeItem> COPPER_PICKAXE = utmItems.ITEMS.register("copper_pickaxe", () -> new PickaxeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    PickaxeItem.createAttributes(TIER_COPPER, 4, -2)
            ))
    );
    public static final Supplier<HoeItem> COPPER_HOE = utmItems.ITEMS.register("copper_hoe", () -> new HoeItem(TIER_COPPER,
            new Item.Properties().attributes(
                    HoeItem.createAttributes(TIER_COPPER, 4, -2)
            ))
    );

    /*
    public static final Supplier<AxeItem> AWESOME_AXE = utmItems.ITEMS.register("awesome_axe", () -> {
            var Tier = utmToolBuilder.buildTier(
                    BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1281, 5f, 3f, 30, () -> Ingredient.EMPTY
            );
            return new AxeItem(Tier, new Item.Properties().attributes(
                            AxeItem.createAttributes(Tier, 4, -2.4f)));
            }
    );
    */

    public static void doNothing() {
        utm.LOGGER.info("[UTM] Tools class loaded, so that it shows up!");
    }
}
