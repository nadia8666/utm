package com.nadia.utm.renderer.glint;

import net.minecraft.client.renderer.MultiBufferSource;
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

    public boolean passUpdate(ItemStack stack, MultiBufferSource.BufferSource bufferSource, boolean changed) {
        T lastState = SUPPLIER != null ? SUPPLIER.apply(THREAD) : THREAD.get();
        T currentState = stack.getOrDefault(COMPONENT.get(), INITIAL_VALUE);

        boolean thisChanged = !Objects.equals(lastState, currentState);

        if (thisChanged) {
            bufferSource.endBatch();
            THREAD.set(currentState);
            changed = true;
        }

        return changed;
    }
}
