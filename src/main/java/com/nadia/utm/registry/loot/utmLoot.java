package com.nadia.utm.registry.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class utmLoot {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "utm");

    public static final Supplier<MapCodec<AddItemModifier>> ADD_ITEM = GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("add_item", AddItemModifier.CODEC);
    public static final Supplier<MapCodec<ReplaceItemModifier>> REPLACE_ITEM = GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("replace_item", ReplaceItemModifier.CODEC);

    public static void register(IEventBus bus) {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}