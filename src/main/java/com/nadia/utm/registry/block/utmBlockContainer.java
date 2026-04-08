package com.nadia.utm.registry.block;

import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.resources.ResourceLocation;
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
    public static final Map<utmBlockContainer<?, ?>, List<String>> DATAGEN_TAGS = new HashMap<>();

    public final String NAME;
    public final DeferredBlock<B> BLOCK;
    public final DeferredItem<I> ITEM;
    private final List<Consumer<? super B>> callbacks;
    public utmBlockContainer(String name, DeferredBlock<B> block, DeferredItem<I> item, List<Consumer<? super B>> callbacks) {
        NAME = name;
        BLOCK = block;
        ITEM = item;
        this.callbacks = callbacks;
    }


    public utmBlockContainer<B, I> onRegister(Consumer<? super B> callback) {
        this.callbacks.add(callback);
        return this;
    }

    /**
     * sets the output stress
     * @return block container
     */
    public utmBlockContainer<B, I> stress(double stress) {
        onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> stress));
        return this;
    }

    /**
     * makes the block drop itself
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> drops() {
        getForDatagen().add("dropSelf");
        return this;
    }

    /**
     * makes the block's item model mirror the block model
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> bModel() {
        getForDatagen().add("blockModel");
        return this;
    }

    /**
     * set the ponder tags
     * @return block container
     * @datagen
     */
    public utmBlockContainer<B, I> ponder(ResourceLocation... ponderTags) {
        List<ResourceLocation> tags = new ArrayList<>(List.of(ponderTags));
        getForDatagen().add("ponderTags:" + String.join(",",tags.stream().map(ResourceLocation::getPath).toList()));
        return this;
    }

    public List<String> getForDatagen() {
        return DATAGEN_TAGS.computeIfAbsent(this, k -> new ArrayList<>());
    }
}
