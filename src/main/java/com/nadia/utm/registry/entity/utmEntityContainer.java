package com.nadia.utm.registry.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class utmEntityContainer<T extends Entity> {
    public DeferredHolder<EntityType<?>, EntityType<T>> REGISTRY_OBJECT;

    public utmEntityContainer(DeferredHolder<EntityType<?>, EntityType<T>> reg) {
        this.REGISTRY_OBJECT = reg;
    }

    public DeferredHolder<EntityType<?>, EntityType<T>> get() {
        return this.REGISTRY_OBJECT;
    }
}
