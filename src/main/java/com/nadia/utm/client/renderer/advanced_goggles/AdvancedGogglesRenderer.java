package com.nadia.utm.client.renderer.advanced_goggles;

import java.util.*;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.events.OxygenPayloadEvent;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.item.AdvancedGogglesItem;
import com.nadia.utm.networking.payloads.GetOxygenPayload;
import com.nadia.utm.networking.utmNetworking;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.util.utmLang;
import com.nadia.utm.utm;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.equipment.goggles.IHaveCustomOverlayIcon;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.api.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CClient;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@ForceLoad(dist = Dist.CLIENT, deps = {utmNetworking.class})
public class AdvancedGogglesRenderer {
    public static final LayeredDraw.Layer OVERLAY = AdvancedGogglesRenderer::renderOverlay;

    public static int HOVER_TICKS = 0;
    public static BlockPos LAST_HOVERED = null;
    public static Entity LAST_HOVERED_ENTITY = null;

    public static final Set<PinnedPanel> PINNED_PANELS = new HashSet<>();
    public static PinnedPanel CURRENT_PANEL = null;
    public static int OFF_X = 0;
    public static int OFF_Y = 0;

    public static int LAST_Y = 0;
    public static int LAST_X = 0;
    public static String GAP = "    ";

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.gameMode == null || mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (!AdvancedGogglesItem.isWearingAdvancedGoggles(mc.player)) return;

        ClientLevel world = mc.level;
        boolean crouching = mc.player.isShiftKeyDown();

        renderPanels(guiGraphics, mc, world, crouching);

        HitResult objectMouseOver = mc.hitResult;
        List<Component> tooltip = new ArrayList<>();
        ItemStack item = AllItems.GOGGLES.asStack();

        if (objectMouseOver instanceof BlockHitResult result) {
            LAST_HOVERED_ENTITY = null;
            BlockPos pos = result.getBlockPos();
            if (isPinned(pos)) return;

            int prevHoverTicks = HOVER_TICKS;
            HOVER_TICKS++;
            LAST_HOVERED = pos;

            pos = getPos(world, pos);
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof IHaveCustomOverlayIcon customOverlayIcon) {
                item = customOverlayIcon.getIcon(crouching);
            }

            if (!blockTooltip(world, pos, be, tooltip, crouching)) {
                HOVER_TICKS = 0;
                return;
            }
        } else if (objectMouseOver instanceof EntityHitResult entityResult) {
            LAST_HOVERED = null;
            Entity entity = entityResult.getEntity();
            if (isPinned(entity.getUUID())) return;

            HOVER_TICKS++;
            LAST_HOVERED_ENTITY = entity;
            entityTooltip(entity, tooltip, crouching);

            if (tooltip.isEmpty()) {
                HOVER_TICKS = 0;
                return;
            }
        } else {
            LAST_HOVERED = null;
            LAST_HOVERED_ENTITY = null;
            HOVER_TICKS = 0;
            return;
        }

        CClient cfg = AllConfigs.client();
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int posX = width / 2 + cfg.overlayOffsetX.get();
        int posY = height / 2 + cfg.overlayOffsetY.get();

        LAST_X = posX;
        LAST_Y = posY;

        float fade = Mth.clamp((HOVER_TICKS + deltaTracker.getGameTimeDeltaPartialTick(false)) / 24f, 0, 1);

        drawTooltip(guiGraphics, mc, tooltip, item, posX, posY, fade, false, null);
    }

    private static void renderPanels(GuiGraphics guiGraphics, Minecraft mc, ClientLevel world, boolean isShifting) {
        List<PinnedPanel> forRemoval = new ArrayList<>();

        for (PinnedPanel panel : PINNED_PANELS) {
            List<Component> tooltip = new ArrayList<>();
            ItemStack item = AllItems.GOGGLES.asStack();

            if (panel.IS_ENTITY) {
                Entity entity = getEntity(world, panel.ENTITY_ID);
                if (entity == null || !entity.isAlive()) {
                    forRemoval.add(panel);
                    continue;
                }
                entityTooltip(entity, tooltip, isShifting);
            } else {
                BlockPos pos = getPos(world, panel.POS);
                BlockEntity be = world.getBlockEntity(pos);
                if (be == null) {
                    forRemoval.add(panel);
                    continue;
                }
                if (be instanceof IHaveCustomOverlayIcon customOverlayIcon) {
                    item = customOverlayIcon.getIcon(isShifting);
                }
                blockTooltip(world, pos, be, tooltip, isShifting);
            }

            if (tooltip.isEmpty()) continue;

            drawTooltip(guiGraphics, mc, tooltip, item, panel.X, panel.Y, 1.0f, true, panel);
        }

        for (PinnedPanel panel : forRemoval)
            PINNED_PANELS.remove(panel);
    }

    private static void drawTooltip(GuiGraphics guiGraphics, Minecraft mc, List<Component> tooltip, ItemStack item, int posX, int posY, float fade, boolean pinned, PinnedPanel panel) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        int tooltipTextWidth = 0;
        for (FormattedText textLine : tooltip) {
            int textLineWidth = mc.font.width(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += 2;
            tooltipHeight += (tooltip.size() - 1) * 10;
        }

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        posX = Math.min(posX, width - tooltipTextWidth - 20);
        posY = Math.min(posY, height - tooltipHeight - 20);

        if (pinned && panel != null) {
            panel.WIDTH = tooltipTextWidth;
            panel.HEIGHT = tooltipHeight - 10;
            panel.RENDER_X = posX;
            panel.RENDER_Y = posY;
        }

        CClient cfg = AllConfigs.client();
        Boolean useCustom = cfg.overlayCustomColor.get();
        Color colorBackground = useCustom ? new Color(cfg.overlayBackgroundColor.get()) : BoxElement.COLOR_VANILLA_BACKGROUND.scaleAlpha(.3f);
        Color colorBorderTop = useCustom ? new Color(cfg.overlayBorderColorTop.get()) : new Color(140, 255, 255);
        Color colorBorderBot = useCustom ? new Color(cfg.overlayBorderColorBot.get()) : new Color(25, 205, 210);

        if (fade < 1) {
            pose.translate(Math.pow(1 - fade, 3) * Math.signum(cfg.overlayOffsetX.get() + .5f) * 8, 0, 0);
            colorBackground.scaleAlpha(fade);
            colorBorderTop.scaleAlpha(fade);
            colorBorderBot.scaleAlpha(fade);
        }

        GuiGameElement.of(item).at(posX + 10, posY - 16, 400).render(guiGraphics);
        if (pinned)
            guiGraphics.drawString(mc.font, "x", posX + 12, posY + tooltipHeight - 19, 0xFFFF5555);
        RemovedGuiUtils.drawHoveringText(guiGraphics, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);


        pose.popPose();
    }

    private static boolean blockTooltip(Level world, BlockPos pos, BlockEntity be, List<Component> tooltip, boolean isShifting) {
        boolean hasGoggleInformation = be instanceof IHaveGoggleInformation;
        boolean hasHoveringInformation = be instanceof IHaveHoveringInformation;
        boolean goggleAddedInformation = false;
        boolean hoverAddedInformation = false;

        if (hasGoggleInformation) {
            goggleAddedInformation = ((IHaveGoggleInformation) be).addToGoggleTooltip(tooltip, isShifting);
        }

        if (hasHoveringInformation) {
            if (!tooltip.isEmpty()) tooltip.add(CommonComponents.EMPTY);
            hoverAddedInformation = ((IHaveHoveringInformation) be).addToTooltip(tooltip, isShifting);
            if (goggleAddedInformation && !hoverAddedInformation) tooltip.removeLast();
        }

        if (be instanceof IDisplayAssemblyExceptions) {
            if (((IDisplayAssemblyExceptions) be).addExceptionToTooltip(tooltip)) {
                hasHoveringInformation = true;
                hoverAddedInformation = true;
            }
        }

        if (!hasHoveringInformation) {
            hoverAddedInformation = TrainRelocator.addToTooltip(tooltip, isShifting);
            if (hoverAddedInformation) {
                hasHoveringInformation = true;
                HOVER_TICKS++;
            }
        }

        if ((hasGoggleInformation && !goggleAddedInformation) && (hasHoveringInformation && !hoverAddedInformation)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        if (AllBlocks.PISTON_EXTENSION_POLE.has(state)) {
            Direction[] directions = Iterate.directionsInAxis(state.getValue(PistonExtensionPoleBlock.FACING).getAxis());
            int poles = 1;
            boolean pistonFound = false;
            for (Direction dir : directions) {
                int attachedPoles = PistonExtensionPoleBlock.PlacementHelper.get().attachedPoles(world, pos, dir);
                poles += attachedPoles;
                pistonFound |= world.getBlockState(pos.relative(dir, attachedPoles + 1)).getBlock() instanceof MechanicalPistonBlock;
            }

            if (pistonFound) {
                if (!tooltip.isEmpty()) tooltip.add(CommonComponents.EMPTY);
                CreateLang.translate("gui.goggles.pole_length").text(" " + poles).forGoggles(tooltip);
            }
        }

        return !tooltip.isEmpty();
    }

    private static void entityTooltip(Entity entity, List<Component> tooltip, boolean isShifting) {
        Integer oxygen = entity.getExistingDataOrNull(utmAttachments.TEMPORARY_OXYGEN);
        if (oxygen != null) {
            utmLang.text((oxygen / 20) + "s").style(ChatFormatting.AQUA).space().add(utmLang.text("of oxygen remaining").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        }

        if (!tooltip.isEmpty()) {
            utmLang.text("").add(entity.getDisplayName()).space().text("Info:").forGoggles(tooltip);

            tooltip.addFirst(tooltip.getLast());
            tooltip.removeLast();
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.level().getGameTime() % 20 == 0) {
            PacketDistributor.sendToServer(new GetOxygenPayload(entity.getUUID().toString(), 0));
        }
    }

    public static BlockPos getPos(Level level, BlockPos pos) {
        BlockState targetedState = level.getBlockState(pos);
        if (targetedState.getBlock() instanceof IProxyHoveringInformation proxy)
            return proxy.getInformationSource(level, pos, targetedState);
        return pos;
    }

    private static Entity getEntity(Level level, UUID uuid) {
        for (Entity entity : ((ClientLevel) level).entitiesForRendering()) {
            if (entity.getUUID().equals(uuid)) return entity;
        }
        return null;
    }

    private static boolean isPinned(BlockPos pos) {
        return PINNED_PANELS.stream().anyMatch(p -> !p.IS_ENTITY && p.POS.equals(pos));
    }

    private static boolean isPinned(UUID uuid) {
        return PINNED_PANELS.stream().anyMatch(p -> p.IS_ENTITY && p.ENTITY_ID.equals(uuid));
    }

    public static class PinnedPanel {
        public boolean IS_ENTITY;
        public BlockPos POS;
        public UUID ENTITY_ID;
        public int X, Y;
        public int RENDER_X = 0, RENDER_Y = 0;
        public int WIDTH, HEIGHT;

        public PinnedPanel(BlockPos pos, int x, int y) {
            this.IS_ENTITY = false;
            this.POS = pos;
            this.X = x;
            this.Y = y;
        }

        public PinnedPanel(UUID entityId, int x, int y) {
            this.IS_ENTITY = true;
            this.ENTITY_ID = entityId;
            this.X = x;
            this.Y = y;
        }
    }

    static {
        utmEvents.register(RegisterGuiLayersEvent.class, event -> event.registerAboveAll(utm.key("advanced_goggle_info"), AdvancedGogglesRenderer::renderOverlay));
        utmEvents.register(OxygenPayloadEvent.class, event -> {
            GetOxygenPayload payload = event.PAYLOAD;
            UUID uuid = UUID.fromString(payload.id());
            int oxygen = payload.oxygen();

            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Entity entity = getEntity(mc.level, uuid);
                if (entity != null) {
                    if (oxygen == -1)
                        entity.removeData(utmAttachments.TEMPORARY_OXYGEN);
                    else
                        entity.setData(utmAttachments.TEMPORARY_OXYGEN, oxygen);
                }
            }
        });
    }
}