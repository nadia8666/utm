package com.nadia.utm.registry.item;

import com.nadia.utm.item.LockedSchematicItem;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.registry.song.utmSongs;
import com.nadia.utm.registry.tags.utmTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
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
            AIR_COMPRESSOR = register("air_compressor").generated();

    // elytra trim
    public static final utmItemContainer<Item> ELYTRA_TRIM_VEIN = register("elytra_trim_vein").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_OUTWARD = register("elytra_trim_outward").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_LESSER = register("elytra_trim_lesser").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_SPADES = register("elytra_trim_spades").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_HEARTSTWINGS = register("elytra_trim_heartstwings").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_ECOLOGIST = register("elytra_trim_ecologist").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES),
            ELYTRA_TRIM_ROADRUNNER = register("elytra_trim_roadrunner").generated().tags(utmTags.ITEM.ELYTRA_TRIM_TEMPLATES, ItemTags.TRIM_TEMPLATES);

    public static final utmItemContainer<Item> FIDDLECORE = register("fiddlecore").generated(),
            FLOATINGCORE = register("floatingcore").generated();
    public static final utmItemContainer<LockedSchematicItem> INCREDI = register("incredipak", () -> new LockedSchematicItem("incredipak.nbt")).generated();

    // discs
    public static final utmItemContainer<Item> MUSIC_DISC_TEARS = register("music_disc_tears", new Item.Properties()
            .stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(utmSongs.TEARS)).disc().tags(Tags.Items.MUSIC_DISCS),
            MUSIC_DISC_LAVA_CHICKEN = register("music_disc_lava_chicken", new Item.Properties()
                    .stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(utmSongs.LAVA_CHICKEN)).disc().tags(Tags.Items.MUSIC_DISCS),
            MUSIC_DISC_UNDERTALE = register("music_disc_undertale", new Item.Properties()
                    .stacksTo(1).rarity(Rarity.EPIC).jukeboxPlayable(utmSongs.UNDERTALE)).disc().tags(Tags.Items.MUSIC_DISCS);

    // fluid
    public static final utmItemContainer<BucketItem> LIQUID_OXYGEN_BUCKET = register("liquid_oxygen_bucket",
            () -> new BucketItem(utmFluids.LIQUID_OXYGEN.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))).generated().tags(Tags.Items.BUCKETS);
    public static final utmItemContainer<BucketItem> MOLTEN_STEEL_BUCKET = register("molten_steel_bucket",
            () -> new BucketItem(utmFluids.MOLTEN_STEEL.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))).generated().tags(Tags.Items.BUCKETS);

    public static final utmItemContainer<Item> ARID_INGOT = register("arid_ingot").generated();

    // resources
    public static final utmItemContainer<Item> RAW_ALUMINUM = register("raw_aluminum").generated(),
            RAW_MAGNESIUM = register("raw_magnesium").generated(),
            ALUMINUM_INGOT = register("aluminum_ingot").generated(),
            MAGNESIUM_INGOT = register("magnesium_ingot").generated(),
            STEEL_INGOT = register("steel_ingot").generated(),
            ALUMINUM_SHEET = register("aluminum_sheet").generated(),
            MAGNESIUM_SHEET = register("magnesium_sheet").generated(),
            STEEL_SHEET = register("steel_sheet").generated();
}