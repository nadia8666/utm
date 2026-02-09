package com.nadia.utm.registry.recipe;

import com.nadia.utm.recipe.ElytraTrimRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class utmRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "utm");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ElytraTrimRecipe>> ELYTRA_TRIM_SERIALIZER =
            RECIPE_SERIALIZERS.register("elytra_trim", ElytraTrimRecipe.Serializer::new);
}
