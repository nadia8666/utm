package com.nadia.utm.client.compat.emi;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.utm;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.nadia.utm.Config.HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT;

public class OxygenFurnaceCategory implements EmiRecipe {
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            utm.key("oxygen_furnace"),
            EmiStack.of(utmBlocks.OXYGEN_FURNACE.ITEM.get())
    );

    private final EmiIngredient LOX = EmiStack.of(utmFluids.LIQUID_OXYGEN.get(), 1000);
    private final EmiIngredient IRON = EmiStack.of(Items.IRON_INGOT, 10);
    private final EmiIngredient COAL = EmiStack.of(Items.COAL);
    private final EmiStack OUTPUT = EmiStack.of(utmFluids.MOLTEN_STEEL.get(), 1000);
    private final ResourceLocation ID = utm.key("/oxygen_furnace/steel");

    public OxygenFurnaceCategory() {}

    @Override
    public EmiRecipeCategory getCategory() { return CATEGORY; }

    @Override
    public ResourceLocation getId() { return this.ID; }

    @Override
    public List<EmiIngredient> getInputs() { return List.of(LOX, IRON, COAL); }

    @Override
    public List<EmiStack> getOutputs() { return List.of(OUTPUT); }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 35+16;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var levelTarget = HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT.getAsInt();

        widgets.addSlot(LOX, 5, 16);
        widgets.addTexture(EmiTexture.PLUS, 27, 2+16);
        widgets.addSlot(IRON, 53-8, 8-5);
        widgets.addSlot(COAL, 53-8, 8+16+3+3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 72, 16);
        widgets.addSlot(OUTPUT, 100, 16).recipeContext(this);

        Component text = Component.translatable("utm.emi.oxygen_furnace_duration", 10);
        int textWidth = Minecraft.getInstance().font.width(text);
        int center = (170 / 2) - (textWidth / 2);
        widgets.addText(text, center, 35, 0xFFFFFF, true);
    }
}