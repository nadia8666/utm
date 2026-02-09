package com.nadia.utm.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.recipe.utmRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;

public class ElytraTrimRecipe extends SmithingTransformRecipe {
    public Ingredient template;
    public Ingredient base;
    public Ingredient addition;
    public ItemStack result;

    public ElytraTrimRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        super(template, base, addition, result);

        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public @NotNull ItemStack assemble(SmithingRecipeInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack result = input.base().copy();

        // see you can do utmItems.ELYTRA_TRIM_VEIN.get() == input.template().getItem() etc but i dont really want to do that.
        result.set(utmDataComponents.ELYRA_TRIM_TYPE.get(), switch(input.template().getItem().toString()) {
            case "utm:elytra_trim_vein" -> "vein";
            case "utm:elytra_trim_outward" -> "outward";
            case "utm:elytra_trim_lesser" -> "lesser";
            default -> throw new IllegalStateException("[UTM] Unknown trim type: " + input.template().getItem());
        });

        // same case here
        result.set(utmDataComponents.ELYRA_TRIM_COLOR.get(), switch(input.addition().getItem().toString()) {
            case "minecraft:iron_ingot" -> 0xABABAB;
            case "minecraft:copper_ingot" -> 0xFFBE3D;
            case "minecraft:gold_ingot" -> 0xFFE924;
            case "minecraft:lapis_lazuli" -> 0x2F24FF;
            case "minecraft:emerald" -> 0x24FF2B;
            case "minecraft:diamond" -> 0x24F0FF;
            case "minecraft:netherite_ingot" -> 0x121212;
            case "minecraft:redstone" -> 0xEB1515;
            case "minecraft:quartz" -> 0xFFFFFF;
            case "minecraft:amethyst_shard" -> 0xB116E0;
            default -> 0xFFFFFF; // in case of Mod: kill everything
        });


        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return utmRecipes.ELYTRA_TRIM_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<ElytraTrimRecipe> {
        public Serializer() {}

        private static final MapCodec<ElytraTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
                Ingredient.CODEC.fieldOf("base").forGetter(r -> r.base),
                Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result)
        ).apply(inst, ElytraTrimRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ElytraTrimRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.template,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.addition,
                ItemStack.STREAM_CODEC, r -> r.result,
                ElytraTrimRecipe::new
        );


        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ElytraTrimRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public @NotNull MapCodec<ElytraTrimRecipe> codec() {
            return CODEC;
        }

        private static ElytraTrimRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new ElytraTrimRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ElytraTrimRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.base);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.addition);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}