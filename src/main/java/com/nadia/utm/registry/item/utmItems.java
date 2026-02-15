package com.nadia.utm.registry.item;

import com.nadia.utm.item.LockedSchematicItem;
import com.nadia.utm.registry.song.utmSongs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class utmItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("utm");

    public static final DeferredItem<Item> UNFINISHED_GLINT_TABLE = ITEMS.registerSimpleItem("unfinished_glint_table");

    // elytra trim
    public static final DeferredItem<Item> ELYTRA_TRIM_VEIN = ITEMS.registerSimpleItem("elytra_trim_vein");
    public static final DeferredItem<Item> ELYTRA_TRIM_OUTWARD = ITEMS.registerSimpleItem("elytra_trim_outward");
    public static final DeferredItem<Item> ELYTRA_TRIM_LESSER = ITEMS.registerSimpleItem("elytra_trim_lesser");
    public static final DeferredItem<Item> ELYTRA_TRIM_SPADES = ITEMS.registerSimpleItem("elytra_trim_spades");
    public static final DeferredItem<Item> ELYTRA_TRIM_HEARTSTWINGS = ITEMS.registerSimpleItem("elytra_trim_heartstwings");
    public static final DeferredItem<Item> ELYTRA_TRIM_ECOLOGIST = ITEMS.registerSimpleItem("elytra_trim_ecologist");
    public static final DeferredItem<Item> ELYTRA_TRIM_ROADRUNNER = ITEMS.registerSimpleItem("elytra_trim_roadrunner");

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
}
