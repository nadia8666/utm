package com.nadia.utm.gui.glint;

import com.nadia.utm.registry.sound.utmSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GlintSlider extends AbstractWidget {
    public static final ResourceLocation MARKER_IMAGE = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/container/glint_table_slider.png");

    private final float minRange;
    private final float maxRange;
    private float currentValue;
    private final Font font;
    private final Runnable onSync;
    public boolean isDragging = false;
    private long lastUpdate = 0;
    private float lastValue = 0;

    public GlintSlider(Font font, int x, int y, int height, float min, float max, float initialValue, Runnable onSync) {
        super(x, y, 4, height, Component.empty());
        this.minRange = min;
        this.maxRange = max;
        this.currentValue = initialValue;
        this.onSync = onSync;
        this.font = font;

        this.lastValue = currentValue;
    }

    /** @return 0.0 - 1.0 alpha for the range*/
    public float getAlpha() {
        return Mth.clamp((currentValue - minRange) / (maxRange - minRange), 0.0f, 1.0f);
    }

    public float getActualValue() {
        return currentValue;
    }

    public void setActualValue(float value) {
        if (!this.isFocused()) {
            this.currentValue = Mth.clamp(value, minRange, maxRange);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.clicked(mouseX, mouseY)) {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F)
            );

            this.updateFromMouse(mouseY);
            isDragging = true;

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
        }

        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    private void updateFromMouse(double mouseY) {
        float pct = 1.0f - (float) ((mouseY - this.getY()) / (double) this.height);
        float newValue = minRange + (Mth.clamp(pct, 0.0f, 1.0f) * (maxRange - minRange));

        if (this.currentValue != newValue) {
            this.currentValue = newValue;

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdate > .25) {
                float diff = Mth.abs(currentValue - lastValue);
                float pitch = 0.75f + diff/maxRange;
                Minecraft.getInstance().getSoundManager().play(
                        SimpleSoundInstance.forUI(utmSounds.SLIDER_TICK.get(), pitch, .8f)
                );

                lastValue = currentValue;
                lastUpdate = currentTime;
            }

            this.onSync.run();
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (isDragging) {
            this.updateFromMouse(mouseY);
        }

        int markerOffset = (int) (this.height * (1.0f - getAlpha()));
        int drawX = this.getX()-1;
        int drawY = this.getY() + markerOffset - 2;
        guiGraphics.blit(MARKER_IMAGE,
                drawX, drawY,
                4, 4,
                0, 0,
                8, 8,
                8, 8
        );

        if (this.isHovered()) {
            String tooltipText = String.format("%.1f", this.getActualValue());

            guiGraphics.renderTooltip(this.font,
                    Component.literal(tooltipText), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput ignored) {}
}