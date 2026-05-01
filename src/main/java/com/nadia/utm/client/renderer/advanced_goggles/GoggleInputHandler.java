package com.nadia.utm.client.renderer.advanced_goggles;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.item.AdvancedGogglesItem;
import com.nadia.utm.registry.input.utmKeyMappings;
import com.nadia.utm.utm;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@ForceLoad(dist = Dist.CLIENT)
public class GoggleInputHandler {
    static {
        utmEvents.register(InputEvent.MouseButton.Pre.class, event -> changed(event.getButton(), event.getAction(), -1, -1));

        utmEvents.register(ScreenEvent.MouseButtonPressed.Pre.class, event -> {
            if (changed(event.getButton(), GLFW.GLFW_PRESS, (int) event.getMouseX(), (int) event.getMouseY())) {
                event.setCanceled(true);
            }
        });

        utmEvents.register(ScreenEvent.MouseButtonReleased.Pre.class, event -> changed(event.getButton(), GLFW.GLFW_RELEASE, (int) event.getMouseX(), (int) event.getMouseY()));

        utmEvents.register(ScreenEvent.MouseDragged.Pre.class, event -> {
            if (AdvancedGogglesRenderer.CURRENT_PANEL != null) {
                AdvancedGogglesRenderer.CURRENT_PANEL.X = (int) event.getMouseX() - AdvancedGogglesRenderer.OFF_X;
                AdvancedGogglesRenderer.CURRENT_PANEL.Y = (int) event.getMouseY() - AdvancedGogglesRenderer.OFF_Y;
                event.setCanceled(true);
            }
        });

        utmEvents.register(InputEvent.Key.class, event -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.screen != null) return;
            if (!AdvancedGogglesItem.isWearingAdvancedGoggles(mc.player)) return;

            while (utmKeyMappings.ADVANCED_GOGGLES_PIN_KEY.consumeClick()) {
                if (AdvancedGogglesRenderer.LAST_HOVERED != null) {
                    AdvancedGogglesRenderer.PINNED_PANELS.add(new AdvancedGogglesRenderer.PinnedPanel(AdvancedGogglesRenderer.LAST_HOVERED, AdvancedGogglesRenderer.LAST_X, AdvancedGogglesRenderer.LAST_Y));
                } else if (AdvancedGogglesRenderer.LAST_HOVERED_ENTITY != null) {
                    AdvancedGogglesRenderer.PINNED_PANELS.add(new AdvancedGogglesRenderer.PinnedPanel(AdvancedGogglesRenderer.LAST_HOVERED_ENTITY.getUUID(), AdvancedGogglesRenderer.LAST_X, AdvancedGogglesRenderer.LAST_Y));
                }
            }
        });
    }

    private static boolean changed(int button, int action, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        utm.LOGGER.warn("{}",mc.screen);
        if (mc.player == null || (mc.screen != null && !(mc.screen instanceof ChatScreen))) return false;
        if (!AdvancedGogglesItem.isWearingAdvancedGoggles(mc.player)) return false;

        if (mouseX == -1 || mouseY == -1) {
            mouseX = (int) (mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
            mouseY = (int) (mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
        }

        if (action == GLFW.GLFW_RELEASE) {
            AdvancedGogglesRenderer.CURRENT_PANEL = null;
            return false;
        }

        if (action == GLFW.GLFW_PRESS) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                for (AdvancedGogglesRenderer.PinnedPanel panel : AdvancedGogglesRenderer.PINNED_PANELS) {
                    if (mouseX >= panel.RENDER_X + 10 && mouseX <= panel.RENDER_X + panel.WIDTH + 15 && mouseY >= panel.RENDER_Y - 16 && mouseY <= panel.RENDER_Y + panel.HEIGHT) {
                        if (mouseX <= panel.RENDER_X + 10 + (mc.font.width("x")) && mouseY >= panel.RENDER_Y + panel.HEIGHT - mc.font.lineHeight) {
                            AdvancedGogglesRenderer.PINNED_PANELS.remove(panel);
                        } else {
                            AdvancedGogglesRenderer.CURRENT_PANEL = panel;
                            AdvancedGogglesRenderer.OFF_X = mouseX - panel.X;
                            AdvancedGogglesRenderer.OFF_Y = mouseY - panel.Y;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}