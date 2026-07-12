package com.nadia.utm.registry.item;

import com.nadia.utm.registry.item.tool.utmToolBuilder;
import com.nadia.utm.registry.utmRegistry;
import com.nadia.utm.utm;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class utmArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR = DeferredRegister.create(Registries.ARMOR_MATERIAL,"utm");

    public static Holder<ArmorMaterial> register(String name, ArmorMaterial material) {
        return ARMOR.register(name, () -> material);
    }

    public static Holder<ArmorMaterial> build(String name, EnumMap<ArmorItem.Type, Integer> typeProtection,
                                                       int enchantability, float toughness, float knockbackResistance,
                                                       Supplier<Item> ingredientItem) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(utm.MODID, name);
        Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;
        Supplier<Ingredient> ingredient = () -> Ingredient.of(ingredientItem.get());
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(location));

        EnumMap<ArmorItem.Type, Integer> typeMap = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            typeMap.put(type, typeProtection.get(type));
        }

        return register(name, new ArmorMaterial(typeProtection, enchantability, equipSound, ingredient, layers, toughness, knockbackResistance));
    }

    public static final Holder<ArmorMaterial> ULTRA_ARMOR_MATERIAL = build("ultra",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, 1);
                attribute.put(ArmorItem.Type.LEGGINGS, 2);
                attribute.put(ArmorItem.Type.CHESTPLATE, 4);
                attribute.put(ArmorItem.Type.HELMET, 1);
                attribute.put(ArmorItem.Type.BODY, 15);
            }) , 30, 5f, 0.0f, () -> Items.AIR);
    public static final Holder<ArmorMaterial> MEGA_ARMOR_MATERIAL = build("mega",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, 5);
                attribute.put(ArmorItem.Type.LEGGINGS, 7);
                attribute.put(ArmorItem.Type.CHESTPLATE, 10);
                attribute.put(ArmorItem.Type.HELMET, 6);
                attribute.put(ArmorItem.Type.BODY, 30);
            }) , 30, -3f, -1.0f, () -> Items.AIR);
    public static final Holder<ArmorMaterial> OMEGA_ARMOR_MATERIAL = build("omega",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, -2);
                attribute.put(ArmorItem.Type.LEGGINGS, -2);
                attribute.put(ArmorItem.Type.CHESTPLATE, -2);
                attribute.put(ArmorItem.Type.HELMET, -2);
                attribute.put(ArmorItem.Type.BODY, 30);
            }) , 999, 0f, 0.125f, () -> Items.AIR);


}
