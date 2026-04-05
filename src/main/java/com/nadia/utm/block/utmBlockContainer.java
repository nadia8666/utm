package com.nadia.utm.block;

import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.function.Consumer;

public class utmBlockContainer<B extends Block, I extends BlockItem> {
    public final DeferredBlock<B> block;
    public final DeferredItem<I> item;
    private final List<Consumer<? super B>> callbacks;
    public utmBlockContainer(DeferredBlock<B> blockTarget, DeferredItem<I> itemTarget, List<Consumer<? super B>> callbacks) {
        block = blockTarget;
        item = itemTarget;
        this.callbacks = callbacks;
    }

    public utmBlockContainer<B, I> onRegister(Consumer<? super B> callback) {
        this.callbacks.add(callback);
        return this;
    }

    public utmBlockContainer<B, I> stress(double stress) {
        onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> stress));
        return this;
    }
}
