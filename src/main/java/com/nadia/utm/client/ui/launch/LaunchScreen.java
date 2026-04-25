package com.nadia.utm.client.ui.launch;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nadia.utm.networking.payloads.LaunchContraptionPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class LaunchScreen extends Screen {
    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("utm", "textures/gui/launch_contraption.png");
    public final int CONTRAPTION_ID;

    public LaunchScreen(Component title, int contraptionID) {
        super(title);
        CONTRAPTION_ID = contraptionID;
    }

    @Override
    protected void init() {
        int x = (width - 176) / 2;
        int y = (height - 76) / 2;

        this.addRenderableWidget(new LaunchButton(x + 71, y + 34, 34, 34, Component.empty(), pressed -> {
            if (!pressed) return;

            PacketDistributor.sendToServer(new LaunchContraptionPayload(CONTRAPTION_ID));
        }));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - 176) / 2;
        int y = (height - 76) / 2;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, 176, 76);

        guiGraphics.drawString(font, Component.translatable("utm.gui.launch_contraption_head"), x + 67, y + 10, 0xFFff5e5e);
        guiGraphics.drawString(font, Component.translatable("utm.gui.launch_contraption_desc"), x + 13, y + 21, 0xFFff5e5e);
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
