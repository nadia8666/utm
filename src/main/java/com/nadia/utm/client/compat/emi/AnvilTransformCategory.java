package com.nadia.utm.client.compat.emi;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.nadia.utm.Config.HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT;

public class AnvilTransformCategory implements EmiRecipe {
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            utm.key("anvil_transformation"),
            EmiStack.of(Items.ANVIL)
    );

    private final EmiIngredient INPUT1, INPUT2;
    private final EmiStack OUTPUT;
    private final ResourceLocation ID;

    public AnvilTransformCategory(ResourceLocation id, ItemStack left, ItemStack right, ItemStack output) {
        this.ID = id;
        this.INPUT1 = EmiStack.of(left);
        this.INPUT2 = EmiStack.of(right);
        this.OUTPUT = EmiStack.of(output);
    }

    @Override
    public EmiRecipeCategory getCategory() { return CATEGORY; }

    @Override
    public ResourceLocation getId() { return this.ID; }

    @Override
    public List<EmiIngredient> getInputs() { return List.of(INPUT1, INPUT2); }

    @Override
    public List<EmiStack> getOutputs() { return List.of(OUTPUT); }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT.getAsInt() > 0 ? 35 : 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var levelTarget = HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT.getAsInt();

        widgets.addSlot(INPUT1, 5, 0);
        widgets.addTexture(EmiTexture.PLUS, 29, 2);
        widgets.addSlot(INPUT2, 50, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 72, 0);
        widgets.addSlot(OUTPUT, 100, 0).recipeContext(this);

        if (levelTarget > 0) {
            boolean requirementSatisfied = Minecraft.getInstance().player != null &&
                    Minecraft.getInstance().player.experienceLevel >= levelTarget;

            Component text = Component.translatable("utm.emi.requires_levels", levelTarget);
            int textWidth = Minecraft.getInstance().font.width(text);
            int center = (125 / 2) - (textWidth / 2);

            widgets.addDrawable(0, 0, 125, 40, (guiGraphics, mouseX, mouseY, delta) -> {
                int padding = 2;
                int boxX1 = center - padding;
                int boxY1 = 23 - padding;
                int boxX2 = center + textWidth + padding;
                int boxY2 = 23 + 8 + padding;

                guiGraphics.fill(boxX1, boxY1, boxX2, boxY2, 0xFF898989);
            });

            widgets.addText(text, center, 23, requirementSatisfied ? 0x7efc20 : 0xFF6060, true);
        }
    }
}