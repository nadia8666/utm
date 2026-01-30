package com.nadia.utm.renderer.glint;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class GlintStateContainer<T> {
    public final DeferredHolder<DataComponentType<?>, DataComponentType<T>> COMPONENT;
    public final ThreadLocal<T> THREAD;
    private final T INITIAL_VALUE;

    public GlintStateContainer (
            DeferredHolder<DataComponentType<?>, DataComponentType<T>> component,
            T initialValue
    ) {
    COMPONENT = component;
    THREAD = ThreadLocal.withInitial(() -> initialValue);
    INITIAL_VALUE = initialValue;
    }

    public boolean tryUpdate(ItemStack stack) {
        var LastState = THREAD.get();
        var CurrentState = stack.getOrDefault(COMPONENT.get(), INITIAL_VALUE);

        var DidChange = LastState != CurrentState;

        if (DidChange) {
            THREAD.set(CurrentState);
        }

        return DidChange;
    }
}
