package com.nadia.utm.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class utmBlockContainer<B extends Block, I extends BlockItem> {
    public final DeferredBlock<B> block;
    public final DeferredItem<I> item;
    public utmBlockContainer(DeferredBlock<B> blockTarget, DeferredItem<I> itemTarget) {
        block = blockTarget;
        item = itemTarget;
    }
}
