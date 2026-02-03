package com.nadia.utm.datagen.providers.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.jetbrains.annotations.NotNull;

public class StonecutterRecipeBuilder extends  SimpleRecipeBuilder {
    private final Ingredient ingredient;
    private final String group;

    public StonecutterRecipeBuilder(String group, Ingredient ingredient, ItemStack result) {
        super(result);
        this.group = group;
        this.ingredient = ingredient;
    }

    @Override
    public void save(RecipeOutput output, @NotNull ResourceLocation id) {
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        StonecutterRecipe recipe = new StonecutterRecipe(this.group, this.ingredient, this.result);
        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
    }
}
