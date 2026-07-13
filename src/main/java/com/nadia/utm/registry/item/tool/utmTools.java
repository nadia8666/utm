package com.nadia.utm.registry.item.tool;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.item.*;
import com.nadia.utm.item.BatItem;
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
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.function.Supplier;

import static com.nadia.utm.registry.item.utmArmorMaterials.*;
import static com.nadia.utm.registry.item.utmArmorMaterials.OMEGA_ARMOR_MATERIAL;
import static net.neoforged.neoforge.common.util.AttributeUtil.*;

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




    //tiers

    public static final Tier TIER_COPPER = utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 0, 7f, 2, 67,
            () -> Ingredient.of(Tags.Items.INGOTS_COPPER));



    //copper

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

    public static final utmItemContainer<FiddleheadItem> FIDDLEHEAD = register("fiddlehead", () -> new FiddleheadItem(new Item.Properties().durability(72000).rarity(Rarity.RARE).stacksTo(1)));

    public static final utmItemContainer<NetherytraItem> NETHERYTRA = register("netherytra", () -> new NetherytraItem(new Item.Properties().durability(850).fireResistant().rarity(Rarity.RARE)))
            .tags(Tags.Items.ENCHANTABLES, ItemTags.EQUIPPABLE_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE).generated();

    public static final utmItemContainer<SwordItem> OBSIDIAN_SWORD = register("obsidian_sword", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 4200, 1f, 0f, 5, () -> Ingredient.of(Tags.Items.OBSIDIANS)
                );
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 12, -3.75f)).rarity(Rarity.UNCOMMON));
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

    public static final utmItemContainer<SwordItem> CYCLESWORD_DULL = register("cyclesword_dull", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 400, 1f, 0f, 15, () -> Ingredient.of(Tags.Items.INGOTS_IRON)
                );
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 2.32f, -1f)));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();


    //fusions

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
            } //todo: why does this have two arrow hit sfx?
            //todo: find this out
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS).handheld();

    /// arid
    //todo: you should make all of these fire res

    public static final utmItemContainer<SwordItem> ARID_SWORD = register("arid_sword", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_WOODEN_TOOL, 5000, 8f, 0f, 15, () -> Ingredient.EMPTY
                ); // this doesnt swing forsome reason
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 4f, 0.44f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.SWORDS).handheld();

    public static final utmItemContainer<PickaxeItem> ARID_PICKAXE = register("arid_pickaxe", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_STONE_TOOL, 6500, 9.5f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new PickaxeItem(tier, new Item.Properties().attributes(
                        PickaxeItem.createAttributes(tier, 8, -3f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.PICKAXES).handheld();
    public static final utmItemContainer<AxeItem> ARID_AXE = register("arid_axe", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_STONE_TOOL, 2500, 9.5f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new AxeItem(tier, new Item.Properties().attributes(
                        AxeItem.createAttributes(tier, 17, -3.4f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.AXES).handheld();
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
                        BlockTags.INCORRECT_FOR_STONE_TOOL, 50000, 1f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new ShovelItem(tier, new Item.Properties().attributes(
                        ShovelItem.createAttributes(tier, 4, -2.4f)).rarity(Rarity.RARE));
            } //tills btw
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE, ItemTags.HOES).handheld();
    public static final utmItemContainer<AridTridentItem> ARID_TRIDENT = register("arid_trident", () -> {
        return new AridTridentItem(new Item.Properties().rarity(utmRarities.MYSTIC.getValue()).stacksTo(1).durability(1024).component(utmDataComponents.THROWING_SPEAR_MODEL, "arid_trident")); //condense pls
            }
    ).tags(Tags.Items.ENCHANTABLES, utmTags.ITEM.ADD_PIERCING);
    public static final utmItemContainer<Paxel> ARID_PAXEL = register("arid_paxel", () -> { // no texture yet. Theres no crying until the end
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_IRON_TOOL, 10000, 10.25f, 0f, 45, () -> Ingredient.EMPTY
                );
                return new Paxel(tier, new Item.Properties().attributes(
                        Paxel.createAttributes(tier, 10, -3f)).rarity(utmRarities.MYSTIC.getValue()));
            } //todo: investigate why you and arid pickaxe have no visible mining speed buff over netherite pickaxe
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, ItemTags.PICKAXES).handheld();
    public static final utmItemContainer<AridSlingshotItem> ARID_SLINGSHOT = register("arid_slingshot", () -> {
                return new AridSlingshotItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(5000));
            } //figure out why Shooting upwards is baad
    ).tags(Tags.Items.ENCHANTABLES, utmTags.ITEM.ADD_MULTISHOT, utmTags.ITEM.ADD_POWER).handheld();
    public static final utmItemContainer<Item> ARID_BAT = register("arid_bat", new Item.Properties().attributes(BatItem.createAttributes(utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 2048, 2f, 0f, 15, () -> Ingredient.EMPTY
    ), 7.75f, -2.9f, 10f)).stacksTo(1).rarity(Rarity.RARE)).tags(ItemTags.FIRE_ASPECT_ENCHANTABLE).handheld(); // don't make it a swordd !!!




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
    //todo: make it not whiff the first attack every time


    /// spears

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
                        PickaxeItem.createAttributes(tier, 4, -2.8f)).rarity(utmRarities.MYSTIC.getValue()));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.MINING_ENCHANTABLE, ItemTags.PICKAXES, ItemTags.DURABILITY_ENCHANTABLE).handheld();
    public static final utmItemContainer<Item> GLOVE = register("glove", new Item.Properties().attributes(SwordItem.createAttributes(utmToolBuilder.buildTier(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1000, 2f, 0f, 15, () -> Ingredient.of(Items.LEATHER)
    ), 0, 4f)).stacksTo(1)).tags(ItemTags.DURABILITY_ENCHANTABLE).generated(); // don't make it a swordd !!!
    //todo: make it have less knockback
    public static final utmItemContainer<BundleofHisItem> BUNDLE_OF_HIS = register("bundle_of_his", () -> {
                return new BundleofHisItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
            } //figure out why Shooting upwards is baad
    ).generated();
    public static final utmItemContainer<StoneSlingshotItem> SLINGSHOT = register("slingshot", () -> {
                return new StoneSlingshotItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).durability(256));
            } //figure out why Shooting upwards is baad
    ).tags(Tags.Items.ENCHANTABLES, utmTags.ITEM.ADD_MULTISHOT, utmTags.ITEM.ADD_POWER, ItemTags.DURABILITY_ENCHANTABLE).handheld();

    public static final utmItemContainer<SwordItem> BLACK_KNIFE = register("black_knife", () -> {
                Tier tier = utmToolBuilder.buildTier(
                        BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 5f, 0f, 15, () -> Ingredient.EMPTY
                );
                return new SwordItem(tier, new Item.Properties().attributes(
                        SwordItem.createAttributes(tier, 7f, -2.4f)).rarity(Rarity.RARE));
            }
    ).tags(Tags.Items.ENCHANTABLES, ItemTags.SWORD_ENCHANTABLE, ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.SWORDS);

    //armor

    //ArmorItem.Type.HELMET.getDurability(factor)
    public static final utmItemContainer<ArmorItem> MEGA_HELMET = register("mega_helmet", () -> new ArmorItem(MEGA_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
            new Item.Properties().durability(10).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR, ItemTags.HEAD_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> MEGA_CHESTPLATE = register("mega_chestplate", () -> new ArmorItem(MEGA_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
            new Item.Properties().durability(10).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR, ItemTags.CHEST_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> MEGA_LEGGINGS = register("mega_leggings", () -> new ArmorItem(MEGA_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
            new Item.Properties().durability(10).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR, ItemTags.LEG_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> MEGA_BOOTS = register("mega_boots", () -> new ArmorItem(MEGA_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
            new Item.Properties().durability(10).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.FOOT_ARMOR, ItemTags.FOOT_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();

        public static final utmItemContainer<ArmorItem> ULTRA_HELMET = register("ultra_helmet", () -> new ArmorItem(ULTRA_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
            new Item.Properties().durability(11).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR, ItemTags.HEAD_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> ULTRA_CHESTPLATE = register("ultra_chestplate", () -> new ArmorItem(ULTRA_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
            new Item.Properties().durability(11).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR, ItemTags.CHEST_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> ULTRA_LEGGINGS = register("ultra_leggings", () -> new ArmorItem(ULTRA_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
            new Item.Properties().durability(11).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR, ItemTags.LEG_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> ULTRA_BOOTS = register("ultra_boots", () -> new ArmorItem(ULTRA_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
            new Item.Properties().durability(11).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.FOOT_ARMOR, ItemTags.FOOT_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();

    //todo: bulwark conversion (helmet to chestplate, etc)
    public static final utmItemContainer<ArmorItem> OMEGA_HELMET = register("omega_helmet", () -> new ArmorItem(OMEGA_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
            new Item.Properties().attributes(ItemAttributeModifiers.builder().add(
                            Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.withDefaultNamespace("armor.oh"),
                            0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.HEAD).build()
            ).durability(12).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.HEAD_ARMOR, ItemTags.HEAD_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> OMEGA_CHESTPLATE = register("omega_chestplate", () -> new ArmorItem(OMEGA_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
            new Item.Properties().attributes(ItemAttributeModifiers.builder().add(
                            Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.withDefaultNamespace("armor.oc"),
                            0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.CHEST).build()
            ).durability(12).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR, ItemTags.CHEST_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> OMEGA_LEGGINGS = register("omega_leggings", () -> new ArmorItem(OMEGA_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
            new Item.Properties().attributes(ItemAttributeModifiers.builder().add(
                            Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.withDefaultNamespace("armor.ol"),
                            0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.LEGS).build()
            ).durability(12).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.LEG_ARMOR, ItemTags.LEG_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();
    public static final utmItemContainer<ArmorItem> OMEGA_BOOTS = register("omega_boots", () -> new ArmorItem(OMEGA_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
            new Item.Properties().attributes(ItemAttributeModifiers.builder().add(
                            Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.withDefaultNamespace("armor.ob"),
                            0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.FEET).build()
            ).durability(12).rarity(utmRarities.MYSTIC.getValue()))).tags(Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.ARMOR_ENCHANTABLE, ItemTags.FOOT_ARMOR, ItemTags.FOOT_ARMOR_ENCHANTABLE, ItemTags.TRIMMABLE_ARMOR).trimmable();

}