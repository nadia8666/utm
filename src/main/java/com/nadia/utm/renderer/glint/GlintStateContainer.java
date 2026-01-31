package com.nadia.utm.renderer.glint;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

public class GlintStateContainer<T> {
    public final DeferredHolder<DataComponentType<?>, DataComponentType<T>> COMPONENT;
    public final ThreadLocal<T> THREAD;
    private final T INITIAL_VALUE;
    private final @Nullable() Function<ThreadLocal<T>, T> SUPPLIER;

    public GlintStateContainer (
            DeferredHolder<DataComponentType<?>, DataComponentType<T>> component,
            T initialValue,
            @Nullable() Function<ThreadLocal<T>, T> sup
    ) {
        COMPONENT = component;
        THREAD = ThreadLocal.withInitial(() -> initialValue);
        INITIAL_VALUE = initialValue;
        SUPPLIER = sup;
    }

    public boolean tryUpdate(ItemStack stack) {
        var LastState = SUPPLIER != null ? SUPPLIER.apply(THREAD) : THREAD.get();
        var CurrentState = stack.getOrDefault(COMPONENT.get(), INITIAL_VALUE);

        var DidChange = !Objects.equals(LastState, CurrentState);
        if (DidChange) {
            THREAD.set(CurrentState);
        }

        return DidChange;
    }
}
