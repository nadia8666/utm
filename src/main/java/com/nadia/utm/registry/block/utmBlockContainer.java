package com.nadia.utm.registry.block;

import com.nadia.utm.registry.item.utmItemContainer;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class utmBlockContainer<B extends Block, I extends BlockItem> {
    public static final List<utmBlockContainer<?, ?>> ALL_BLOCKS = new ArrayList<>();
    public static final Map<utmBlockContainer<?, ?>, List<String>> DATAGEN_TAGS = new HashMap<>();
    public final List<TagKey<Block>> DATAGEN_BLOCK_TAGS = new ArrayList<>();

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
    public utmBlockContainer<B, I> stress(double stress) {
        onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> stress));
        return this;
    }

    /**
     * makes the block drop itself
     *
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> drops() {
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
    public utmBlockContainer<B, I> copyItemModel() {
        getForDatagen().add("blockModel");
        return this;
    }

    /**
     * automatically generates block model and state as a child of block/block
     *
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> bModel() {
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
    @SafeVarargs
    public final utmBlockContainer<B, I> tags(TagKey<Block>... tags) {
        this.DATAGEN_BLOCK_TAGS.addAll(List.of(tags));
        return this;
    }

    public List<String> getForDatagen() {
        return DATAGEN_TAGS.computeIfAbsent(this, k -> new ArrayList<>());
    }
}
