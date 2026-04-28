package com.nadia.utm.registry.block;

import com.nadia.utm.block.displaylink.utmDisplaySources;
import com.nadia.utm.registry.item.utmItemContainer;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class utmBlockContainer<B extends Block, I extends BlockItem> {
    public static final List<utmBlockContainer<?, ?>> ALL_BLOCKS = new ArrayList<>();
    public static final Map<utmBlockContainer<?, ?>, List<String>> DATAGEN_TARGETS = new HashMap<>();
    public final List<TagKey<Block>> DATAGEN_BLOCK_TAGS = new ArrayList<>();
    public final List<TagKey<Item>> DATAGEN_ITEM_TAGS = new ArrayList<>();

    public final String NAME;
    public final DeferredBlock<B> BLOCK;
    public final DeferredItem<I> ITEM;
    private final List<Consumer<? super B>> callbacks;

    public utmBlockContainer(String name, DeferredBlock<B> block, DeferredItem<I> item, List<Consumer<? super B>> callbacks) {
        NAME = name;
        BLOCK = block;
        ITEM = item;
        this.callbacks = callbacks;

        ALL_BLOCKS.add(this);
    }

    public void onRegister(Consumer<? super B> callback) {
        this.callbacks.add(callback);
    }

    /**
     * sets the output stress
     *
     * @return block container
     */
    public utmBlockContainer<B, I> stress(double stress, boolean capacity) {
        onRegister(b -> (capacity ? BlockStressValues.CAPACITIES : BlockStressValues.IMPACTS).register(b, () -> stress));
        return this;
    }

    public utmBlockContainer<B, I> stress(double stress) {
        stress(stress, false);
        return this;
    }

    /**
     * makes the block drop itself
     *
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> dropSelf() {
        getForDatagen().add("dropSelf");
        return this;
    }

    /**
     * makes the block drop an item
     *
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> dropOre(utmItemContainer<?> item) {
        getForDatagen().add("dropOre:" + item.ITEM().getId().getPath());
        return this;
    }

    /**
     * makes the block's item model mirror the block model
     *
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> inheritModel() {
        getForDatagen().add("blockModel");
        return this;
    }

    /**
     * automatically generates block model and state as a child of block/block
     *
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> cube() {
        getForDatagen().add("blockModelState");
        return this;
    }

    /**
     * set the ponder tags
     *
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> ponder(ResourceLocation... ponderTags) {
        List<ResourceLocation> tags = new ArrayList<>(List.of(ponderTags));
        getForDatagen().add("ponderTags:" + String.join(",", tags.stream().map(ResourceLocation::getPath).toList()));
        return this;
    }

    /**
     * requires pickaxe to break
     *
     * @return block container
     * @datagen
     * @see #mineTier(int)
     */
    public utmBlockContainer<B, I> minePick() {
        getForDatagen().add("mine:pickaxe");
        return this;
    }

    /**
     * requires axe to break
     *
     * @return block container
     * @datagen
     * @see #mineTier(int)
     */
    public utmBlockContainer<B, I> mineAxe() {
        getForDatagen().add("mine:axe");
        return this;
    }

    /**
     * requires shovel to break
     *
     * @return block container
     * @datagen
     * @see #mineTier(int)
     */
    public utmBlockContainer<B, I> mineShovel() {
        getForDatagen().add("mine:shovel");
        return this;
    }

    /**
     * requires how to break
     *
     * @return block container
     * @datagen
     * @see #mineTier(int)
     */
    public utmBlockContainer<B, I> mineHoe() {
        getForDatagen().add("mine:hoe");
        return this;
    }

    /**
     * combined with {@link #minePick()}/{@link #mineAxe()}/{@link #mineShovel()}/{@link #mineHoe()} ()} to require a tool of tier x or higher
     *
     * @param tier 1, 2, 3 : stone, iron, diamond
     * @datagen
     */
    public utmBlockContainer<B, I> mineTier(int tier) {
        getForDatagen().add("tier:" + tier);
        return this;
    }

    /**
     * add tags
     *
     * @param tags tags
     * @datagen
     */
    @SuppressWarnings("unchecked")
    public final utmBlockContainer<B, I> tags(TagKey<?>... tags) {
        for (TagKey<?> tag : tags) {
            if (tag.isFor(Registries.BLOCK)) {
                this.DATAGEN_BLOCK_TAGS.add((TagKey<Block>) tag);
            } else if (tag.isFor(Registries.ITEM)) {
                this.DATAGEN_ITEM_TAGS.add((TagKey<Item>) tag);
            }
        }
        return this;
    }

    /**
     * mark a block as valid display source, will automatically attach all related blockentities.
     *
     * @param source source factory
     */
    public final utmBlockContainer<B, I> displaySource(Supplier<DisplaySource> source, boolean entity) {
        utmDisplaySources.ALL_SOURCES_BLOCK.put(this, () -> List.of(source.get()));

        if (entity)
            utmDisplaySources.ALL_SOURCES_ENTITY.put(this, () -> List.of(source.get()));

        return this;
    }

    public List<String> getForDatagen() {
        return DATAGEN_TARGETS.computeIfAbsent(this, k -> new ArrayList<>());
    }
}
