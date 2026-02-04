package com.nadia.utm.datagen.providers;

import com.nadia.utm.datagen.providers.recipe.StonecutterRecipeBuilder;
import com.nadia.utm.registry.block.utmBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class utmRecipeProvider extends RecipeProvider {
    public utmRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        new StonecutterRecipeBuilder(
                "iron_ingot",  // apparently group clumps together recipes with the same group idk what that means so.
                Ingredient.of(new ItemStack(Items.IRON_INGOT)),
                new ItemStack(utmBlocks.GRATE.item.get(), 3)).unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(output);
    }
}
