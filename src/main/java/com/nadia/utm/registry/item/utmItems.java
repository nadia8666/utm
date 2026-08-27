package com.nadia.utm.registry.item;

import com.nadia.utm.item.*;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.registry.song.utmSongs;
import com.nadia.utm.registry.tags.utmTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.TorchBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class utmItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("utm");

    public static <I extends Item> utmItemContainer<I> register(String name, Supplier<I> func) {
        DeferredItem<I> item = ITEMS.register(name, func);
        return new utmItemContainer<>(name, item);
    }

    public static utmItemContainer<Item> register(String name, Item.Properties properties) {
        DeferredItem<Item> item = ITEMS.registerItem(name, Item::new, properties);
        return new utmItemContainer<>(name, item);
    }

    public static utmItemContainer<Item> register(String name) {
        DeferredItem<Item> item = ITEMS.registerItem(name, Item::new, new Item.Properties());
        return new utmItemContainer<>(name, item);
    }

    public static Item fromName(String name) throws Exception {
        for (DeferredHolder<Item, ? extends Item> item : ITEMS.getEntries().stream().toList()) {
            if (item.getId().getPath().equals(name))
                return item.get();
        }
        throw new Exception("[UTM] Unable to find item with name: " + name);
    }

    // ingredients
    public static final utmItemContainer<Item> UNFINISHED_GLINT_TABLE = register("unfinished_glint_table").generated(),
            UNFINISHED_ARID_INGOT = register("unfinished_arid_ingot").generated(),
            AIR_COMPRESSOR = register("air_compressor").generated(),
            UNASSABELED = register("unassabeled").generated(),
            FUSING_PROCESS = register("fusing_process").generated();


    // elytra trim
    public static final utmItemContainer<Item> ELYTRA_TRIM_VEIN = register("elytra_trim_vein").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_OUTWARD = register("elytra_trim_outward").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_LESSER = register("elytra_trim_lesser").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_SPADES = register("elytra_trim_spades").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_HEARTSTWINGS = register("elytra_trim_heartstwings").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_ECOLOGIST = register("elytra_trim_ecologist").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_ROADRUNNER = register("elytra_trim_roadrunner").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES);

    public static final utmItemContainer<Item> FIDDLECORE = register("fiddlecore").generated(),
            FLOATINGCORE = register("floatingcore").generated(),
            ELECTROMAGNETICCORE = register("electromagneticcore").generated();
    public static final utmItemContainer<LockedSchematicItem> INCREDI = register("incredipak", () -> new LockedSchematicItem("incredipak.nbt")).generated();

    public static final utmItemContainer<AdvancedGogglesItem> GOGGLES = register("adv_goggles", () -> new AdvancedGogglesItem(new Item.Properties().stacksTo(1)))
            .generated().tags();

    // discs
    public static final utmItemContainer<Item> MUSIC_DISC_TEARS = register("music_disc_tears", new Item.Properties()
            .stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(utmSongs.TEARS)).disc().tags(Tags.Items.MUSIC_DISCS),
            MUSIC_DISC_LAVA_CHICKEN = register("music_disc_lava_chicken", new Item.Properties()
                    .stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(utmSongs.LAVA_CHICKEN)).disc().tags(Tags.Items.MUSIC_DISCS),
            MUSIC_DISC_UNDERTALE = register("music_disc_undertale", new Item.Properties()
                    .stacksTo(1).rarity(Rarity.EPIC).jukeboxPlayable(utmSongs.UNDERTALE)).disc().tags(Tags.Items.MUSIC_DISCS),
            MUSIC_DISC_UNDERTALE2 = register("music_disc_undertale2", new Item.Properties()
                    .stacksTo(1).rarity(Rarity.EPIC).jukeboxPlayable(utmSongs.UNDERTALE2)).disc().tags(Tags.Items.MUSIC_DISCS);

    // fluid
    public static final utmItemContainer<BucketItem> LIQUID_OXYGEN_BUCKET = register("liquid_oxygen_bucket",
            () -> new BucketItem(utmFluids.LIQUID_OXYGEN.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))).generated().tags(Tags.Items.BUCKETS);
    public static final utmItemContainer<BucketItem> MOLTEN_STEEL_BUCKET = register("molten_steel_bucket",
            () -> new BucketItem(utmFluids.MOLTEN_STEEL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))).generated().tags(Tags.Items.BUCKETS);
    public static final utmItemContainer<AntiwaterBucketItem> ANTIWATER_BUCKET = register("antiwater_bucket",
            () -> new AntiwaterBucketItem(utmFluids.ANTIWATER.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))).generated().tags(Tags.Items.BUCKETS);
    public static final utmItemContainer<BucketItem> MUNDANEWATER_BUCKET = register("mundanewater_bucket",
            () -> new BucketItem(utmFluids.MUNDANEWATER.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))).generated().tags(Tags.Items.BUCKETS);


    //bullets
    public static final utmItemContainer<AridIngotItem> ARID_INGOT = register("arid_ingot", () -> new AridIngotItem(new Item.Properties())).generated();
    public static final utmItemContainer<AridIngotItem> ARID_BULLET = register("arid_bullet", () -> new AridIngotItem(new Item.Properties())).generated();
    public static final utmItemContainer<PebbleItem> PEBBLE_BULLET = register("pebble_bullet", () -> new PebbleItem(new Item.Properties().stacksTo(1))).generated();


    // resources
    public static final utmItemContainer<Item> RAW_ALUMINUM = register("raw_aluminum").generated(),
            RAW_MAGNESIUM = register("raw_magnesium").generated(),
            ALUMINUM_INGOT = register("aluminum_ingot").generated(),
            MAGNESIUM_INGOT = register("magnesium_ingot").generated(),
            STEEL_INGOT = register("steel_ingot").generated(),
            ALUMINUM_SHEET = register("aluminum_sheet").generated(),
            MAGNESIUM_SHEET = register("magnesium_sheet").generated(),
            STEEL_SHEET = register("steel_sheet").generated(),
            COPPER_PLATING = register("copper_plating").generated(),
            ELECTRO_CANISTER = register("electro_canister").generated(),
            ANTIWATER_CANISTER = register("antiwater_canister", new Item.Properties().stacksTo(1)).generated(), //todo: think about making this lossy, also same as below // noob - nadia
            ANTIWATER_CONDUIT = register("antiwater_conduit", new Item.Properties().stacksTo(1)).generated(),
            ELECTRO_CANISTER_LIGHT = register("electro_canister_light", new Item.Properties().rarity(utmRarities.MYSTIC.getValue())).generated(), //todo: needs antiwater rarity
            ANTIWATER_CANISTER_LIGHT = register("antiwater_canister_light", new Item.Properties().stacksTo(4).rarity(utmRarities.MYSTIC.getValue())).generated(), // same as above
            PARTICASE = register("particase", new Item.Properties().stacksTo(1)).generated(),
            ELECTRASE = register("electrase", new Item.Properties().stacksTo(1)).generated(),
            POSITRASE = register("positrase", new Item.Properties().stacksTo(1)).generated(),
            BLAPIS_BLAZULI = register("blapis_blazuli", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(-5).saturationModifier(-2).build()).rarity(utmRarities.BLUE.getValue())).generated();


    // apples
    public static final utmItemContainer<Item> ANCIENT_APPLE = register("ancient_apple",
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build())).generated(),
            BIOME_APPLE = register("biome_apple",
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build())).generated(),
            PROGRESSION_APPLE = register("progression_apple",
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build())).generated(),
            MOB_APPLE = register("mob_apple",
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build())).generated(),
            CLOCKWORK_APPLE = register("clockwork_apple",
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build())).generated(),
            NAUGHT_APPLE = register("naught_apple", //needs mystic
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build()).rarity(utmRarities.MYSTIC.getValue())).generated(),
            CORRECTIVE_APPLE = register("corrective_apple", //needs mystic
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build()).rarity(utmRarities.MYSTIC.getValue())).generated(),
            TOOL_APPLE = register("tool_apple",
                   new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build())).generated(),
            ARMOR_APPLE = register("armor_apple",
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(2).saturationModifier(2).build())).generated(),
            AUGMENTATION_APPLE = register("augmentation_apple", // needs mystic
                    new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(64).saturationModifier(64).build()).rarity(utmRarities.MYSTIC.getValue())).generated();


    public static final utmItemContainer<BappleItem> BAPPLE = register("bapple", // neebs dlue
            () -> new BappleItem(new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(-2).saturationModifier(-8132023).fast().build()).rarity(utmRarities.BLUE.getValue()))).generated();

    public static final utmItemContainer<Item> YELLOW_COAL = register("yellow_coal").generated();
    public static final utmItemContainer<Item> NICE_TORCH = register("nice_torch").generated();


    //TODO: IMPORTANT!!!! START PUTTING WATER AS OUTPUT IN ANTIWATER RECIPES! IMPORTANT! START DOING THAT!
    //ok
    //no

}