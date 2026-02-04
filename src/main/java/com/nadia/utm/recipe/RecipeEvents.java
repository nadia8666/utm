package com.nadia.utm.recipe;

import com.nadia.utm.registry.block.utmBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import static com.nadia.utm.Config.HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT;

@EventBusSubscriber(modid = "utm")
public class RecipeEvents {
    @SubscribeEvent
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
