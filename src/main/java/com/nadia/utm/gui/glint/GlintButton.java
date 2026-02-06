package com.nadia.utm.gui.glint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

public class GlintButton extends AbstractWidget {
    public boolean pressed = true;
    public static final ResourceLocation BUTTON_DEFAULT = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/container/glint_table_btn.png");
    public static final ResourceLocation BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/container/glint_table_btn_p.png");
    private final Runnable onSync;

    public GlintButton(int x, int y, int width, int height, Component message, Runnable onSync) {
        super(x, y, width, height, message);

        this.onSync = onSync;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.clicked(mouseX, mouseY)) {
            pressed = !pressed;
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F)
            );

            onSync.run();

            return true;
        }
        return false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.blit(!pressed ? BUTTON_PRESSED : BUTTON_DEFAULT, getX(), getY(), 0, 0, this.width, this.height, 22, 11);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput ignored) {}
}
