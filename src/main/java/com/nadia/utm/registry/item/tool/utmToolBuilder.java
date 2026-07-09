package com.nadia.utm.registry.item.tool;

import com.nadia.utm.utm;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class utmToolBuilder {
    public static Tier buildTier(TagKey<Block> incorrectTier, int durability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        return new SimpleTier(incorrectTier, durability, miningSpeed, attackDamage, enchantability, repairIngredient);
    }




}
