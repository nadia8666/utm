package com.nadia.utm.mixin.compat.create;

import com.nadia.utm.compat.IBasinWithHeatGetter;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {
    @Inject(method = "getMaxFluidInputCount", at = @At("RETURN"), cancellable = true)
    private void utm$awesome(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(10);
    }


    /**
     * @author nadiarr
     * @reason what if i want 30 fluids... :DDD
     */
    @Overwrite
    private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        if (!(basin instanceof IBasinWithHeatGetter heater)) return false;

        boolean isBasinRecipe = recipe instanceof BasinRecipe;
        assert basin.getLevel() != null;
        IItemHandler availableItems = basin.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, basin.getBlockPos(), null);
        IFluidHandler availableFluids = basin.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, basin.getBlockPos(), null);

        if (availableItems == null || availableFluids == null)
            return false;

        BlazeBurnerBlock.HeatLevel heat = heater.utm$getHeatLevel();
        if (isBasinRecipe && !((BasinRecipe) recipe).getRequiredHeat()
                .testBlazeBurner(heat))
            return false;

        List<ItemStack> recipeOutputItems = new ArrayList<>();
        List<FluidStack> recipeOutputFluids = new ArrayList<>();

        List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
        List<SizedFluidIngredient> fluidIngredients =
                isBasinRecipe ? ((BasinRecipe) recipe).getFluidIngredients() : Collections.emptyList();

        for (boolean simulate : Iterate.trueAndFalse) {

            if (!simulate && test)
                return true;

            int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
            int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

            Ingredients:
            for (Ingredient ingredient : ingredients) {
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    if (simulate && availableItems.getStackInSlot(slot)
                            .getCount() <= extractedItemsFromSlot[slot])
                        continue;
                    ItemStack extracted = availableItems.extractItem(slot, 1, true);
                    if (!ingredient.test(extracted))
                        continue;
                    if (!simulate)
                        availableItems.extractItem(slot, 1, false);
                    extractedItemsFromSlot[slot]++;
                    continue Ingredients;
                }

                // something wasn't found
                return false;
            }

            boolean fluidsAffected = false;
            FluidIngredients:
            for (SizedFluidIngredient fluidIngredient : fluidIngredients) {
                int amountRequired = fluidIngredient.amount();

                for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                    FluidStack fluidStack = availableFluids.getFluidInTank(tank);

                    int simulatedRemaining = fluidStack.getAmount() - extractedFluidsFromTank[tank];
                    if (simulatedRemaining <= 0)
                        continue;

                    if (!fluidIngredient.test(fluidStack.copyWithAmount(simulatedRemaining)))
                        continue;

                    int drainedAmount = Math.min(amountRequired, simulatedRemaining);
                    if (!simulate) {
                        fluidStack.shrink(drainedAmount);
                        fluidsAffected = true;
                    }

                    extractedFluidsFromTank[tank] += drainedAmount;
                    amountRequired -= drainedAmount;

                    if (amountRequired != 0)
                        continue;
                    continue FluidIngredients;
                }

                // something wasn't found
                return false;
            }

            if (fluidsAffected) {
                basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
                        .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
                basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
                        .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
            }

            if (simulate) {
                CraftingInput remainderInput = new DummyCraftingContainer(availableItems, extractedItemsFromSlot)
                        .asCraftInput();

                if (recipe instanceof BasinRecipe basinRecipe) {
                    recipeOutputItems.addAll(basinRecipe.rollResults(basin.getLevel().random));

                    for (FluidStack fluidStack : basinRecipe.getFluidResults())
                        if (!fluidStack.isEmpty())
                            recipeOutputFluids.add(fluidStack);
                    for (ItemStack stack : basinRecipe.getRemainingItems(remainderInput))
                        if (!stack.isEmpty())
                            recipeOutputItems.add(stack);

                } else {
                    recipeOutputItems.add(recipe.getResultItem(basin.getLevel()
                            .registryAccess()));

                    if (recipe instanceof CraftingRecipe craftingRecipe) {
                        for (ItemStack stack : craftingRecipe.getRemainingItems(remainderInput))
                            if (!stack.isEmpty())
                                recipeOutputItems.add(stack);
                    }
                }
            }

            if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
                return false;
        }

        return true;
    }
}
