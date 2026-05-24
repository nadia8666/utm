package com.nadia.utm.client.compat.emi;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.registry.tags.utmTags;
import com.nadia.utm.util.EnchantUtil;
import com.nadia.utm.utm;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiAnvilRecipe;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

@EmiEntrypoint
public class utmEmiPlugin implements EmiPlugin {
    public static IJeiRuntime RUNTIME;

    @Override
    public void initialize(EmiInitRegistry registry) {
        EmiPlugin.super.initialize(registry);
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(AnvilTransformCategory.CATEGORY);
        registry.addWorkstation(AnvilTransformCategory.CATEGORY, EmiStack.of(Items.ANVIL));

        registry.addCategory(OxygenFurnaceCategory.CATEGORY);
        registry.addWorkstation(OxygenFurnaceCategory.CATEGORY, EmiStack.of(utmBlocks.OXYGEN_FURNACE.ITEM.get()));

        registry.addRecipe(new AnvilTransformCategory(
                utm.key("/anvil_transform/heavy_metal_anvil"),
                new ItemStack(Items.ANVIL),
                new ItemStack(Items.NETHERITE_INGOT),
                new ItemStack(utmBlocks.HEAVY_METAL_ANVIL.ITEM.get())
        ));

        registry.addRecipe(new OxygenFurnaceCategory());

        registry.addRecipe(new EmiAnvilRecipe(
                EmiStack.of(utmTools.COPPER_THROWING_SPEAR.get()),
                EmiStack.of(utmItems.COPPER_PLATING.get()),
                utm.key("/anvil_repair/copper_throwing_spear")
        ));

        registry.removeRecipes(recipe -> {
            if (recipe.getCategory() == VanillaEmiRecipeCategories.ANVIL_REPAIRING) {
                List<EmiIngredient> inputs = recipe.getInputs();
                if (inputs.size() >= 2) {
                    ItemStack tool = inputs.get(0).getEmiStacks().getFirst().getItemStack();
                    ItemStack book = inputs.get(1).getEmiStacks().getFirst().getItemStack();

                    if (tool.is(utmTags.ITEM.EMI_REMOVE_MENDING))
                        return EnchantUtil.findEnchants(tool, book, Enchantments.MENDING);
                }
            }

            return false;
        });
    }
}
