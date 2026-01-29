package com.nadia.utm.recipe;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.utmRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import static com.nadia.utm.Config.HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT;

@EventBusSubscriber(modid = "utm")
public class RecipeEvents {
    // This example allows repairing a stone pickaxe with a full stack of dirt, consuming half the stack, for 3 levels.
    @SubscribeEvent // on the game event bus
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.is(Items.ANVIL) && right.is(Items.NETHERITE_INGOT) && right.getCount() >= 1) {
            event.setOutput(new ItemStack(utmBlocks.HEAVY_METAL_ANVIL.item.get()));
            event.setMaterialCost(1);
            event.setCost(HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT.getAsInt());
        }
    }
}
