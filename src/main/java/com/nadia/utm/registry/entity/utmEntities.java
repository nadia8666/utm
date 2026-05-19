package com.nadia.utm.registry.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class utmEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, "utm");

    public static <T extends Entity> utmEntityContainer<T> register(String name, Supplier<EntityType<T>> factory) {
        return new utmEntityContainer<>(ENTITY_TYPES.register(name, factory));
    }
}
