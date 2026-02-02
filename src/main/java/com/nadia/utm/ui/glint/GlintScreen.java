package com.nadia.utm.ui.glint;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nadia.utm.networking.GlintSyncPayload;
import com.nadia.utm.registry.data.utmDataComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.List;

public class GlintScreen extends AbstractContainerScreen<GlintMenu> {
    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/container/glint_table.png");

    private static final int GRID_X = 56;
    private static final int GRID_Y = 10;
    private static final int GRID_W = 41;
    private static final int GRID_H = 63;
    private static final int PADDING = 2;
    private static final int SLOT_SIZE = 18; // 16px item + spacing

    private GlintSlider scaleX, scaleY, speedX, speedY, r, g, b;
    private GlintButton additiveBtn;

    private float scrollOffset = 0;

    public GlintScreen(GlintMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        scaleX = this.addRenderableWidget(new GlintSlider(x+107, y+24, 47, 0f, 10f, 1f, this::sync));
        scaleY = this.addRenderableWidget(new GlintSlider(x+113, y+24, 47, 0f, 10f, 1f, this::sync));
        speedX = this.addRenderableWidget(new GlintSlider(x+119, y+24, 47, 0f, 10f, 1f, this::sync));
        speedY = this.addRenderableWidget(new GlintSlider(x+125, y+24, 47, 0f, 10f, 1f, this::sync));
        r = this.addRenderableWidget(new GlintSlider(x+138, y+12, 59, 0f, 255f, 255f, this::sync));
        g = this.addRenderableWidget(new GlintSlider(x+144, y+12, 59, 0f, 255f, 255f, this::sync));
        b = this.addRenderableWidget(new GlintSlider(x+150, y+12, 59, 0f, 255f, 255f, this::sync));

        additiveBtn = this.addRenderableWidget(new GlintButton(x+106, y+11, 22, 11, Component.empty(), this::sync));
    }

    public int rgbToInt(float r, float g, float b) {
        int red = Math.round(r);
        int green = Math.round(g);
        int blue = Math.round(b);
        return (red << 16) | (green << 8) | blue;
    }

    public float intToRGB(int color, int channel) {
        return switch (channel) {
            case 0 -> ((color >> 16) & 0xFF);
            case 1 -> ((color >> 8) & 0xFF);
            case 2 -> (color & 0xFF);
            default -> 0.0f;
        };
    }

    private void sync() {
        menu.COLOR = rgbToInt(r.getActualValue(), g.getActualValue(), b.getActualValue());
        menu.SPEED = new Vector2f(speedX.getActualValue(), speedY.getActualValue());
        menu.SCALE = new Vector2f(scaleX.getActualValue(), scaleY.getActualValue());
        menu.ADDITIVE = additiveBtn.pressed;

        PacketDistributor.sendToServer(new GlintSyncPayload(
                menu.COLOR,
                menu.ADDITIVE,
                menu.SPEED,
                menu.SCALE,
                menu.TYPE
        ));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        sync();

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + GRID_X && mouseX <= x + GRID_X + GRID_W &&
                mouseY >= y + GRID_Y && mouseY <= y + GRID_Y + GRID_H) {

            int totalRows = (int) Math.ceil(GlintMenu.TEXTURES.size() / 2.0);
            int contentHeight = totalRows * SLOT_SIZE;
            int viewHeight = GRID_H - (PADDING * 2);
            int maxScroll = Math.max(0, contentHeight - viewHeight);

            this.scrollOffset = (float) Mth.clamp(this.scrollOffset - scrollY * SLOT_SIZE, 0, maxScroll);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int boxX = x + GRID_X;
        int boxY = y + GRID_Y;

        if (mouseX >= boxX + PADDING && mouseX <= boxX + GRID_W - PADDING &&
                mouseY >= boxY + PADDING && mouseY <= boxY + GRID_H - PADDING) {

            double relativeY = (mouseY - (boxY + PADDING)) + scrollOffset;
            double relativeX = (mouseX - boxX);

            int row = (int) (relativeY / SLOT_SIZE);
            int col = getCol(relativeX);

            if (col != -1 && row >= 0) {
                int index = row * 2 + col;
                if (index < GlintMenu.TEXTURES.size()) {
                    menu.TYPE = GlintMenu.TEXTURES.get(index);
                    sync();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static int getCol(double relativeX) {
        int col = -1;

        double col0End = PADDING + SLOT_SIZE;

        double col1Start = GRID_W - PADDING - SLOT_SIZE;
        double col1End = GRID_W - PADDING;

        if (relativeX >= (double) PADDING && relativeX <= col0End) {
            col = 0;
        } else if (relativeX >= col1Start && relativeX <= col1End) {
            col = 1;
        }
        return col;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.fill(x+157, y+13, x+165, y+71, 0xFF000000 | menu.COLOR);

        ItemStack previewStack = menu.resultContainer.getItem(0);

        if (!previewStack.isEmpty()) {
            int targetX = x + 8;
            int targetY = y + 32;
            int targetSize = 42;
            float scale = targetSize / 16.0f;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(targetX, targetY, 100);
            guiGraphics.pose().scale(scale, scale, 1.0f);
            guiGraphics.renderFakeItem(previewStack, 0, 0);
            guiGraphics.pose().popPose();
        }

        this.renderTextureGrid(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void containerTick() {
        super.containerTick();

        if (!r.isDragging) r.setActualValue(intToRGB(menu.COLOR, 0));
        if (!g.isDragging) g.setActualValue(intToRGB(menu.COLOR, 1));
        if (!b.isDragging) b.setActualValue(intToRGB(menu.COLOR, 2));

        if (!scaleX.isDragging) scaleX.setActualValue(menu.SCALE.x);
        if (!scaleY.isDragging) scaleY.setActualValue(menu.SCALE.y);
        if (!speedX.isDragging) speedX.setActualValue(menu.SPEED.x);
        if (!speedY.isDragging) speedY.setActualValue(menu.SPEED.y);

        additiveBtn.pressed = menu.ADDITIVE;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics ignored, int mouseX, int mouseY) {
        // idiot :)
    }

    private void renderTextureGrid(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ItemStack baseStack = menu.resultContainer.getItem(0);
        if (baseStack.isEmpty()) return;

        List<ResourceLocation> textures = GlintMenu.TEXTURES;

        int boxX = leftPos + GRID_X;
        int boxY = topPos + GRID_Y;

        guiGraphics.enableScissor(boxX, boxY, boxX + GRID_W, boxY + GRID_H);

        int startY = boxY + PADDING - (int)scrollOffset;

        for (int i = 0; i < textures.size(); i++) {
            int col = i % 2;
            int row = i / 2;

            int itemX = (col == 0)
                    ? boxX + PADDING
                    : boxX + GRID_W - PADDING - SLOT_SIZE + 3;

            int itemY = startY + (row * SLOT_SIZE);

            if (itemY + SLOT_SIZE < boxY || itemY > boxY + GRID_H) continue;

            ResourceLocation texture = textures.get(i);
            boolean isSelected = texture.equals(menu.TYPE);
            boolean isHovered = (mouseX >= itemX && mouseX < itemX + SLOT_SIZE &&
                    mouseY >= itemY && mouseY < itemY + SLOT_SIZE &&
                    mouseY >= boxY + PADDING && mouseY <= boxY + GRID_H - PADDING);

            if (isSelected) {
                guiGraphics.fill(itemX - 1, itemY - 1, itemX + 17, itemY + 17, 0xFFCCCCCC);
                guiGraphics.renderOutline(itemX - 1, itemY - 1, 18, 18, 0xFFFFFFFF);
            } else if (isHovered) {
                guiGraphics.fill(itemX, itemY, itemX + 16, itemY + 16, 0xCC96d6e3);
            }

            ItemStack displayStack = baseStack.copy();
            displayStack.set(utmDataComponents.GLINT_TYPE, texture);

            guiGraphics.renderFakeItem(displayStack, itemX, itemY);
        }

        guiGraphics.disableScissor();
    }
}