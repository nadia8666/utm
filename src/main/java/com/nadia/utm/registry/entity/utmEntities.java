package com.nadia.utm.registry.entity;

import com.nadia.utm.entity.spear.ThrownSpearEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class utmEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, "utm");

    public static <T extends Entity> utmEntityContainer<T> register(String name, Supplier<EntityType<T>> factory) {
        return new utmEntityContainer<>(ENTITY_TYPES.register(name, factory));
    }

    public static final utmEntityContainer<ThrownSpearEntity> THROWN_SPEAR = register("thrown_spear", () ->
            EntityType.Builder.of(ThrownSpearEntity::new, MobCategory.MISC).sized(0.5f, 0.5f).build("thrown_spear"));
}
