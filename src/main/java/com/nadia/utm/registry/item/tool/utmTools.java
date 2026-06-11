package com.nadia.utm.registry.item.tool;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.item.AridTridentItem;
import com.nadia.utm.item.FiddleheadItem;
import com.nadia.utm.item.NetherytraItem;
import com.nadia.utm.item.spear.TNTThrowingSpearItem;
import com.nadia.utm.item.spear.ThrowingSpearItem;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.item.utmItemContainer;
import com.nadia.utm.registry.item.utmRarities;
import com.nadia.utm.registry.tags.utmTags;
import com.nadia.utm.registry.utmRegistry;
import com.nadia.utm.tool.CopperSword;
import com.nadia.utm.tool.Paxel;
import com.nadia.utm.tool.ShartSword;
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
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4200, 1f, 0f, 5, () -> Ingredient.of(Tags.Items.OBSIDIANS)
                );
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 12, -3.75f)));
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
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 350, 1f, 0f, 15, () -> Ingredient.of(Tags.Items.INGOTS_IRON)
                );
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 4, -1.125f)));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<AxeItem> ARID_AXE = register("arid_axe", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_STONE_TOOL, 2500, 9.5f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new AxeItem(tier, new Item.Properties().attributes(
                        AxeItem.createAttributes(tier, 17, -3.4f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.AXES).handheld();

    public static final utmItemContainer<SwordItem> ARID_SWORD = register("arid_sword", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 5000, 8f, 0f, 15, () -> Ingredient.EMPTY
                ); // this doesnt swing forsome reason
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 4f, 0.44f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<ShartSword> SHART = register("shart", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 100, 1f, 0f, 15, () -> Ingredient.of(Tags.Items.INGOTS_IRON)
                );
                return new ShartSword(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 5, -2.4f)).rarity(Rarity.UNCOMMON)); //todo: give you a blocking animation
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS, Tags.Items.TOOLS_SHIELD).handheld();
    public static final utmItemContainer<SwordItem> SWORD_OF_KIRK = register("sword_of_kirk", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 256, 1f, 0f, 15, () -> Ingredient.of(Tags.Items.INGOTS_IRON)
                );
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 5, -2.4f)).rarity(Rarity.UNCOMMON));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();
    public static final utmItemContainer<PickaxeItem> ARID_PICKAXE = register("arid_pickaxe", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_STONE_TOOL, 6500, 9.5f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new PickaxeItem(tier, new Item.Properties().attributes(
                        PickaxeItem.createAttributes(tier, 8, -3f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.PICKAXES).handheld();
    public static final utmItemContainer<ShovelItem> ARID_SHOVEL = register("arid_shovel", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_STONE_TOOL, 6500, 10f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new ShovelItem(tier, new Item.Properties().attributes(
                        ShovelItem.createAttributes(tier, 4, -2.4f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.SHOVELS).handheld();
    public static final utmItemContainer<ShovelItem> ARID_HOE = register("arid_hoe", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_STONE_TOOL, 5000, 15f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new ShovelItem(tier, new Item.Properties().attributes(
                        ShovelItem.createAttributes(tier, 4, -2.4f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.HOES).handheld();
    public static final utmItemContainer<AridTridentItem> ARID_TRIDENT = register("arid_trident", () -> {
        return new AridTridentItem(new Item.Properties().stacksTo(1).durability(1024).component(utmDataComponents.THROWING_SPEAR_MODEL, "arid_trident")); //condense pls
            } // todo: arid trident is an antiwater item yet no mystic tag. Add all mystic tags to all POST antiwater stuff. ALSO FIX YOUR PIVOT
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.TRIDENT_ENCHANTABLE);
    public static final utmItemContainer<Paxel> ARID_PAXEL = register("arid_paxel", () -> { // no texture yet. Theres no crying until the end
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_IRON_TOOL, 10000, 10.25f, 0f, 45, () -> Ingredient.EMPTY
                );
                return new Paxel(tier, new Item.Properties().attributes(
                        Paxel.createAttributes(tier, 10, -3f)).rarity(utmRarities.MYSTIC.getValue()));
            } //todo: investigate why you and arid pickaxe have no visible mining speed buff over netherite pickaxe
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.PICKAXES).handheld();

    public static final utmItemContainer<SwordItem> SWORD2 = register("sword2", () -> {
        Tier tier = utmToolBuilder.buildTier(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1000, 1f, 0f, 0, () -> Ingredient.EMPTY);
        return new SwordItem(tier, new Item.Properties().rarity(Rarity.UNCOMMON).durability(1000).attributes(SwordItem.createAttributes(tier, 2, -3.5f)));
    }).handheld();

    public static final utmItemContainer<SwordItem> GLOOMSWORD8 = register("gloomsword8", () -> {
        Tier tier = utmToolBuilder.buildTier(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1, 1f, 0f, 0, () -> Ingredient.EMPTY);
        return new SwordItem(tier, new Item.Properties().rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(tier, 4, -3.6f)));
    }).handheld(); ////todo: change you to like glome sword 2 or somethign because 8 is too much You are not a GS! alternatively give it a new sprite
    /// and make it a gs

    public static final utmItemContainer<SwordItem> SABEL3 = register("sabel3", () -> {
        Tier tier = utmToolBuilder.buildTier(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1500, 1f, 0f, 0, () -> Ingredient.EMPTY);
        return new SwordItem(tier, new Item.Properties().rarity(Rarity.RARE).attributes(SwordItem.createAttributes(tier, 5, -2.8f)));
    });//todo: fix your sutpid Delay on your second attakc adn also your model

    public static final utmItemContainer<ThrowingSpearItem> COPPER_THROWING_SPEAR = register("copper_throwing_spear", () -> new ThrowingSpearItem(new Item.Properties()
            .durability(350)
            .component(utmDataComponents.THROWING_SPEAR_MODEL, "copper_throwing_spear")
            .stacksTo(1), 5.5F, -2.9F)
    ).tags(ItemTags.VANISHING_ENCHANTABLE, utmTags.ITEM.ADD_SPEAR_RECOVERY, utmTags.ITEM.ADD_SPEAR_THROW, utmTags.ITEM.ADD_SHARPNESS);

    public static final utmItemContainer<ThrowingSpearItem> NETHERITE_THROWING_SPEAR = register("netherite_throwing_spear", () -> new ThrowingSpearItem(new Item.Properties()
            .durability(550)
            .component(utmDataComponents.THROWING_SPEAR_MODEL, "netherite_throwing_spear")
            .stacksTo(1), 6.5F, -2.6F)
    ).tags(ItemTags.VANISHING_ENCHANTABLE, utmTags.ITEM.ADD_UNBREAKING, utmTags.ITEM.ADD_SPEAR_RECOVERY, utmTags.ITEM.ADD_SPEAR_THROW, utmTags.ITEM.ADD_SHARPNESS);

    public static final utmItemContainer<TNTThrowingSpearItem> TNT_THROWING_SPEAR = register("tnt_throwing_spear", () -> new TNTThrowingSpearItem(new Item.Properties()
            .component(utmDataComponents.THROWING_SPEAR_MODEL, "tnt_throwing_spear")
            .stacksTo(16), 2.5F, -3.55F)
    ).tags(ItemTags.VANISHING_ENCHANTABLE, utmTags.ITEM.ADD_SPEAR_THROW, utmTags.ITEM.ADD_MENDING);
    public static final utmItemContainer<PickaxeItem> GREENLINE_DIAMOND_PICKAXE = register("greenline_diamond_pickaxe", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8f, 0f, 150, () -> Ingredient.of(Items.DIAMOND)
                ); // that block tag might be wrong hey
                return new PickaxeItem(tier, new Item.Properties().attributes(
                        PickaxeItem.createAttributes(tier, 5, -2.8f)).rarity(utmRarities.MYSTIC.getValue()));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.MINING_ENCHANTABLE, ItemTags.PICKAXES, ItemTags.DURABILITY_ENCHANTABLE).handheld();
    public static final utmItemContainer<Item> GLOVE = register("glove").generated();
}