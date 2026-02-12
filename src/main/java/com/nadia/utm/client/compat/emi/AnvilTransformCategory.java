package com.nadia.utm.client.compat.emi;

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

@SuppressWarnings("unused")
public class AnvilTransformCategory implements EmiRecipe {
    // Use a standard category or create a custom one
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath("utm", "anvil_transformation"),
            EmiStack.of(Items.ANVIL)
    );

    private final EmiIngredient left, right;
    private final EmiStack output;
    private final ResourceLocation id;

    public AnvilTransformCategory(ResourceLocation id, ItemStack left, ItemStack right, ItemStack output) {
        this.id = id;
        this.left = EmiStack.of(left);
        this.right = EmiStack.of(right);
        this.output = EmiStack.of(output);
    }

    @Override
    public EmiRecipeCategory getCategory() { return CATEGORY; }

    @Override
    public ResourceLocation getId() { return this.id; } // EMI can auto-generate IDs

    @Override
    public List<EmiIngredient> getInputs() { return List.of(left, right); }

    @Override
    public List<EmiStack> getOutputs() { return List.of(output); }

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

        widgets.addSlot(left, 5, 0);
        widgets.addTexture(EmiTexture.PLUS, 29, 2);
        widgets.addSlot(right, 50, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 72, 0);
        widgets.addSlot(output, 100, 0).recipeContext(this);

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