package com.nadia.utm.client.ui.launch;

import com.nadia.utm.utm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LaunchButton extends AbstractWidget {
    public boolean pressed = false;
    public static final ResourceLocation BUTTON_DEFAULT = utm.key("textures/gui/launch_btn.png");
    public static final ResourceLocation BUTTON_PRESSED = utm.key("textures/gui/launch_btn_p.png");
    private final Consumer<Boolean> onSync;

    public LaunchButton(int x, int y, int width, int height, Component message, Consumer<Boolean> onSync) {
        super(x, y, width, height, message);

        this.onSync = onSync;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.clicked(mouseX, mouseY) && !pressed) {
            pressed = true;
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F)
            );

            onSync.accept(pressed);

            return true;
        }
        return false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.blit(pressed ? BUTTON_PRESSED : BUTTON_DEFAULT, getX(), getY(), 0, 0, this.width, this.height, 34, 34);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput ignored) {}
}
