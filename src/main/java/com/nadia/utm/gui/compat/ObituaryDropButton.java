package com.nadia.utm.gui.compat;

import com.nadia.utm.networking.DropGravePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class ObituaryDropButton extends AbstractWidget {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/gravestone_descend.png");
    private final DropGravePayload PAYLOAD;
    public ObituaryDropButton(int x, int y, int width, int height, Component message, DropGravePayload payload) {
        super(x, y, width, height, message);
        PAYLOAD = payload;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.clicked(mouseX, mouseY)) {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F)
            );

            PacketDistributor.sendToServer(PAYLOAD);

            return true;
        }
        return false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 100);
        guiGraphics.blit(TEXTURE, getX(), getY(), 0, 0, this.width, this.height, 32, 32);
        pose.popPose();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput ignored) {}
}
