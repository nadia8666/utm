package com.nadia.utm.client.ui;

import com.mojang.authlib.GameProfile;
import com.nadia.utm.Config;
import com.nadia.utm.networking.TabLayerPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = "utm", value = Dist.CLIENT)
public class TabMenuLayer {
    public static List<TabLayerPayload.PlayerData> CACHE = new ArrayList<>();

    @SubscribeEvent
    public static void onRenderLayer(RenderGuiLayerEvent.Pre event) {
        if (!Config.ALTERNATE_TAB_MENU.get()) return;
        if (event.getName().equals(VanillaGuiLayers.TAB_LIST)) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.options.keyPlayerList.isDown()) {
                event.setCanceled(true);
                render(event.getGuiGraphics(), event.getGuiGraphics().guiWidth(), event.getGuiGraphics().guiHeight());
            }
        }
    }

    private static void render(GuiGraphics gui, int width, int ignored) {
        Minecraft mc = Minecraft.getInstance();
        if (CACHE.isEmpty()) return;

        int targetWidth = 0;
        int maxHearts = 10;

        for (TabLayerPayload.PlayerData p : CACHE) {
            targetWidth = Math.max(targetWidth, mc.font.width(p.name()));
            int heartsNeeded = (int) Math.ceil(Math.max(p.maxHealth(), p.health()) / 2.0f);
            maxHearts = Math.max(maxHearts, heartsNeeded);
        }

        int leftPadding = 14;
        int dimWidth = 30;
        int pingWidth = 15;
        int healthWidth = Math.min(80, maxHearts * 8);

        int statsWidth = dimWidth + healthWidth + pingWidth + 20;
        int minWidth = 165;

        int entryWidth = Math.max(minWidth, leftPadding + targetWidth + statsWidth);
        int entryHeight = 12;
        int x = (width - entryWidth) / 2;
        int y = 40;

        for (int i = 0; i < CACHE.size(); i++) {
            TabLayerPayload.PlayerData p = CACHE.get(i);
            int rowY = y + (i * entryHeight);

            gui.fill(x, rowY, x + entryWidth, rowY + entryHeight - 1, 0xAA000000);
            gui.renderOutline(x, rowY, entryWidth, entryHeight - 1, 0xFF000000);

            // player heads
            if (p.online()) {
                var playerInfo = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(p.id());
                ResourceLocation skinTexture = playerInfo != null ? playerInfo.getSkin().texture() : DefaultPlayerSkin.get(p.id()).texture();

                gui.setColor(1f, 1f, 1f, 1f);
                PlayerFaceRenderer.draw(gui, skinTexture, x + 2, rowY + 2, 7, true, false);
            } else {
                gui.setColor(1f, 1f, 1f,0.5f);
                PlayerFaceRenderer.draw(gui,
                        mc.getSkinManager().getInsecureSkin(new GameProfile(p.id(), p.name())).texture(),
                        x + 2, rowY + 2, 7, true, false);
                gui.setColor(1f, 1f, 1f, 1f);
            }

            int nameColor = p.online() ? 0xFFFFFFFF : 0xFF707070;
            gui.drawString(mc.font, p.name(), x + leftPadding, rowY + 2, nameColor, false);

            int statsX = x + leftPadding + targetWidth + 10;

            if (p.online()) {
                String dim = p.dimension();
                String dimStr = dim.contains(":") ? dim.substring(dim.indexOf(':') + 1) : dim;
                String shortDim = switch (dimStr) {
                    case "overworld" -> "OVR";
                    case "the_nether" -> "NTH";
                    case "the_end" -> "END";
                    default -> dimStr.length() > 3 ? dimStr.substring(0, 3).toUpperCase() : dimStr.toUpperCase();
                };

                gui.drawString(mc.font, shortDim, statsX, rowY + 2, 0xFFFFFFFF, false);
                renderHealth(gui, statsX + 25, rowY + 1, p.health(), p.maxHealth());
                renderPing(gui, x + entryWidth - 12, rowY + 1, p.ping());
            } else {
                gui.drawString(mc.font, "OFFLINE", x + entryWidth - 45, rowY + 2, 0xFF404040, false);
            }
        }
    }

    private static final ResourceLocation HEART_EMPTY = ResourceLocation.withDefaultNamespace("hud/heart/container");
    private static final ResourceLocation HEART_FULL = ResourceLocation.withDefaultNamespace("hud/heart/full");
    private static final ResourceLocation HEART_HALF = ResourceLocation.withDefaultNamespace("hud/heart/half");
    private static final ResourceLocation HEART_ABSORB_FULL = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full");
    private static final ResourceLocation HEART_ABSORB_HALF = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half");

    private static void renderHealth(GuiGraphics gui, int x, int y, float health, float maxHealth) {
        float max = Math.max(maxHealth, health);
        int totalHealth = Mth.ceil(max / 2.0f);

        float maxWidth = 80.0f;
        float spacing = totalHealth > 10 ? maxWidth / totalHealth : 8.0f;

        for (int i = 0; i < totalHealth; i++) {
            gui.pose().pushPose();
            gui.pose().translate(0, 0, i * 0.05f);

            int tgtX = x + (int)(i * spacing);
            float heartVal = i * 2;

            gui.blitSprite(HEART_EMPTY, tgtX, y, 9, 9);

            if (heartVal < maxHealth) {
                float fillState = health - heartVal;
                if (fillState >= 2) {
                    gui.blitSprite(HEART_FULL, tgtX, y, 9, 9);
                } else if (fillState >= 1) {
                    gui.blitSprite(HEART_HALF, tgtX, y, 9, 9);
                }
            }

            else if (heartVal < health) {
                float fillState = health - heartVal;
                if (fillState >= 2) {
                    gui.blitSprite(HEART_ABSORB_FULL, tgtX, y, 9, 9);
                } else if (fillState >= 1) {
                    gui.blitSprite(HEART_ABSORB_HALF, tgtX, y, 9, 9);
                }
            }

            gui.pose().popPose();
        }
    }

    private static final ResourceLocation PING_5 = ResourceLocation.withDefaultNamespace("icon/ping_5");
    private static final ResourceLocation PING_4 = ResourceLocation.withDefaultNamespace("icon/ping_4");
    private static final ResourceLocation PING_3 = ResourceLocation.withDefaultNamespace("icon/ping_3");
    private static final ResourceLocation PING_2 = ResourceLocation.withDefaultNamespace("icon/ping_2");
    private static final ResourceLocation PING_1 = ResourceLocation.withDefaultNamespace("icon/ping_1");
    private static final ResourceLocation PING_UNKNOWN = ResourceLocation.withDefaultNamespace("icon/ping_unknown");

    private static void renderPing(GuiGraphics gui, int x, int y, int ping) {
        ResourceLocation sprite;

        // this is stupid thank you minecraft
        if (ping < 0) {
            sprite = PING_UNKNOWN;
        } else if (ping < 150) {
            sprite = PING_5;
        } else if (ping < 300) {
            sprite = PING_4;
        } else if (ping < 600) {
            sprite = PING_3;
        } else if (ping < 1000) {
            sprite = PING_2;
        } else {
            sprite = PING_1;
        }

        gui.blitSprite(sprite, x, y, 10, 8);
    }
}
