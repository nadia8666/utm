package com.nadia.utm.client.ui.oxygen_furnace;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nadia.utm.gui.OxygenFurnaceMenu;
import com.nadia.utm.util.PoseUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper;
import org.jetbrains.annotations.NotNull;

public class OxygenFurnaceScreen extends AbstractContainerScreen<OxygenFurnaceMenu> {
    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/container/oxygen_furnace.png");
    public static final ResourceLocation FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/container/oxygen_furnace_fire.png");
    public static final ResourceLocation TANK_TEXTURE = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/container/oxygen_furnace_tank.png");

    public OxygenFurnaceScreen(OxygenFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        drawTank(guiGraphics, menu.blockEntity.LOX.getPrimaryHandler().getFluid(), x + 8, y + 9);
        drawTank(guiGraphics, menu.blockEntity.STEEL.getPrimaryHandler().getFluid(), x + 152, y + 9);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int progress = (int) (menu.getUIProgress() * 49);
        if (progress > 0) guiGraphics.blit(FIRE_TEXTURE, x + 58, y + 47, 0, 0, progress, 7, 49, 7);

        new PoseUtil(guiGraphics.pose()).push().translate(0, 0, 150)
                .run(() -> guiGraphics.blit(TANK_TEXTURE, x, y, 0, 0, imageWidth, imageHeight)).pop();
    }

    private void drawTank(GuiGraphics guiGraphics, FluidStack fluidStack, int x, int y) {
        if (fluidStack.isEmpty()) return;

        IClientFluidTypeExtensions data = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation texture = data.getStillTexture(fluidStack);

        int fluidColor = data.getTintColor(fluidStack);
        int scaledHeight = (int) ((float) fluidStack.getAmount() / 1000 * 68);
        GuiHelper.renderTiledFluidTextureAtlas(guiGraphics, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture), fluidColor, x, y + (68 - scaledHeight), scaledHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}