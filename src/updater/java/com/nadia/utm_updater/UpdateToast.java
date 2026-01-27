package com.nadia.utm_updater;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class UpdateToast implements Toast {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private final Component title;
    private final Component description;

    public UpdateToast(String version) {
        this.title = Component.literal("UTM "+version+" Installed");
        this.description = Component.literal("Restart Minecraft!");
    }

    @Override
    public @NotNull Visibility render(GuiGraphics graphics, ToastComponent component, long timeSinceLastVisible) {
        graphics.blitSprite(TEXTURE, 0, 0, this.width(), this.height());
        Font font = component.getMinecraft().font;

        graphics.drawString(font, title, 5, 7, 0xFFFF00, true);
        graphics.drawString(font, description, 5, 18, 0xFFFFFF, false);

        return timeSinceLastVisible >= 15000L ? Visibility.HIDE : Visibility.SHOW;
    }
}