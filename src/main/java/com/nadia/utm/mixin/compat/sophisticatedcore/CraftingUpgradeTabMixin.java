package com.nadia.utm.mixin.compat.sophisticatedcore;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.Button;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.*;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeTab;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper.ICONS;

@Mixin(CraftingUpgradeTab.class)
public abstract class CraftingUpgradeTabMixin extends UpgradeSettingsTab<CraftingUpgradeContainer> {
    protected CraftingUpgradeTabMixin(CraftingUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
    }

    @Unique
    private static final ClientTooltipPositioner LEFT_SIDE_TOOLTIP_POSITIONER = new ClientTooltipPositioner() {
        @Override
        public @NotNull Vector2ic positionTooltip(int guiWidth, int guiHeight, int mouseX, int mouseY, int tooltipWidth, int tooltipHeight) {
            Vector2i tooltipTopLeft = (new Vector2i(mouseX, mouseY)).add(12, -12);
            this.positionTooltip(guiHeight, tooltipTopLeft, tooltipWidth, tooltipHeight);
            return tooltipTopLeft;
        }

        private void positionTooltip(int guiHeight, Vector2i tooltipTopLeft, int tooltipWidth, int tooltipHeight) {
            tooltipTopLeft.x = Math.max(tooltipTopLeft.x - 24 - tooltipWidth, 4);

            int i = tooltipHeight + 3;
            if (tooltipTopLeft.y + i > guiHeight) {
                tooltipTopLeft.y = guiHeight - i;
            }
        }
    };

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void utm$init(CraftingUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, ButtonDefinition.Toggle<Boolean> shiftClickTargetButton, ButtonDefinition.Toggle<Boolean> refillCraftingGridButton, CallbackInfo ci) {
        CraftingUpgradeTab tab = (CraftingUpgradeTab) (Object) this;

        addHideableChild(new Button(new Position(x + 21 + 18, y + 24), new ButtonDefinition(
                refillCraftingGridButton.getDimension(),
                refillCraftingGridButton.getBackgroundTexture(),
                refillCraftingGridButton.getHoveredBackgroundTexture(),
                new TextureBlitData(ICONS, new Position(1, 1), Dimension.SQUARE_256, new UV(224, 48), Dimension.SQUARE_16)
        ), button -> {
            if (button == 0 && minecraft != null && minecraft.gameMode != null && minecraft.player != null) {
                for (int i = 0; i < 9; i++) {
                    Slot slot = upgradeContainer.getSlots().get(i);
                    if (slot.hasItem()) {
                        minecraft.gameMode.handleInventoryMouseClick(
                                screen.getMenu().containerId,
                                slot.index,
                                0,
                                ClickType.QUICK_MOVE,
                                minecraft.player
                        );
                    }
                }
            }
        }) {
            @Override
            protected List<Component> getTooltip() {
                return List.of(Component.translatable("utm.gui.empty_grid_backpack"));
            }
        });
    }
}