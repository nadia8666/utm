package com.nadia.utm.registry.entity;

import com.nadia.utm.entity.projectile.AridStoneEntity;
import com.nadia.utm.entity.projectile.PebbleEntity;
import com.nadia.utm.entity.spear.ThrownAridTrident;
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
            EntityType.Builder.<ThrownSpearEntity>of((type, level) -> new ThrownSpearEntity(level), MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20).build("thrown_spear"));
    public static final utmEntityContainer<ThrownAridTrident> THROWN_ARID_TRIDENT = register("thrown_arid_trident", () ->
            EntityType.Builder.<ThrownAridTrident>of((type, level) -> new ThrownAridTrident(level), MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(1).build("thrown_arid_trident"));

    public static final utmEntityContainer<AridStoneEntity> ARID_STONE = register("arid_stone", () ->
            EntityType.Builder.<AridStoneEntity>of((type, level) -> new AridStoneEntity(level), MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(10).build("arid_stone"));
    public static final utmEntityContainer<PebbleEntity> PEBBLE = register("pebble", () ->
            EntityType.Builder.<PebbleEntity>of((type, level) -> new PebbleEntity(level), MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(10).build("pebble"));


}
