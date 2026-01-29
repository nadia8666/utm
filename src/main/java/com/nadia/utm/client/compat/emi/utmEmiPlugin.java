package com.nadia.utm.client.compat.emi;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.utmRegistry;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

        registry.addRecipe(new AnvilTransformCategory(
                ResourceLocation.fromNamespaceAndPath("utm", "/anvil_transform/heavy_metal_anvil"),
                new ItemStack(Items.ANVIL),
                new ItemStack(Items.NETHERITE_INGOT),
                new ItemStack(utmBlocks.HEAVY_METAL_ANVIL.item.get())
        ));
    }
}
