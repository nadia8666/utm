package com.nadia.utm.registry.item;

import com.nadia.utm.item.LockedSchematicItem;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.registry.song.utmSongs;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class utmItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("utm");

    // ingredients
    public static final DeferredItem<Item> UNFINISHED_GLINT_TABLE = ITEMS.registerSimpleItem("unfinished_glint_table"),
    UNFINISHED_ARID_INGOT = ITEMS.registerSimpleItem("unfinished_arid_ingot"),
    AIR_COMPRESSOR = ITEMS.registerSimpleItem("air_compressor");

    // elytra trim
    public static final DeferredItem<Item> ELYTRA_TRIM_VEIN = ITEMS.registerSimpleItem("elytra_trim_vein"),
            ELYTRA_TRIM_OUTWARD = ITEMS.registerSimpleItem("elytra_trim_outward"),
            ELYTRA_TRIM_LESSER = ITEMS.registerSimpleItem("elytra_trim_lesser"),
            ELYTRA_TRIM_SPADES = ITEMS.registerSimpleItem("elytra_trim_spades"),
            ELYTRA_TRIM_HEARTSTWINGS = ITEMS.registerSimpleItem("elytra_trim_heartstwings"),
            ELYTRA_TRIM_ECOLOGIST = ITEMS.registerSimpleItem("elytra_trim_ecologist"),
            ELYTRA_TRIM_ROADRUNNER = ITEMS.registerSimpleItem("elytra_trim_roadrunner");

    public static final DeferredItem<Item> FIDDLECORE = ITEMS.registerSimpleItem("fiddlecore");
    public static final DeferredItem<Item> FLOATINGCORE = ITEMS.registerSimpleItem("floatingcore");
    public static final DeferredItem<LockedSchematicItem> INCREDI = ITEMS.register("incredipak", () -> new LockedSchematicItem("incredipak.nbt"));

    // discs
    public static final DeferredItem<Item> MUSIC_DISC_TEARS = ITEMS.registerSimpleItem("music_disc_tears", new Item.Properties()
            .stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(utmSongs.TEARS));
    public static final DeferredItem<Item> MUSIC_DISC_LAVA_CHICKEN = ITEMS.registerSimpleItem("music_disc_lava_chicken", new Item.Properties()
            .stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(utmSongs.LAVA_CHICKEN));
    public static final DeferredItem<Item> MUSIC_DISC_UNDERTALE = ITEMS.registerSimpleItem("music_disc_undertale", new Item.Properties()
            .stacksTo(1).rarity(Rarity.EPIC).jukeboxPlayable(utmSongs.UNDERTALE));

    // fluid
    public static final DeferredItem<Item> LIQUID_OXYGEN_BUCKET = ITEMS.register("liquid_oxygen_bucket",
            () -> new BucketItem(utmFluids.LIQUID_OXYGEN.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> MOLTEN_STEEL_BUCKET = ITEMS.register("molten_steel_bucket",
            () -> new BucketItem(utmFluids.MOLTEN_STEEL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> ARID_INGOT = ITEMS.registerSimpleItem("arid_ingot");

    // resources
    public static final DeferredItem<Item> RAW_ALUMINUM = ITEMS.registerSimpleItem("raw_aluminum"),
            RAW_MAGNESIUM = ITEMS.registerSimpleItem("raw_magnesium"),
            ALUMINUM_INGOT = ITEMS.registerSimpleItem("aluminum_ingot"),
            STEEL_INGOT = ITEMS.registerSimpleItem("steel_ingot"),
            MAGNESIUM_INGOT = ITEMS.registerSimpleItem("magnesium_ingot"),
            ALUMINUM_SHEET = ITEMS.registerSimpleItem("aluminum_sheet"),
            MAGNESIUM_SHEET = ITEMS.registerSimpleItem("magnesium_sheet"),
            STEEL_SHEET = ITEMS.registerSimpleItem("steel_sheet");
}
