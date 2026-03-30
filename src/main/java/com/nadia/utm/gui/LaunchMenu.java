package com.nadia.utm.gui;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.ui.utmMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LaunchMenu extends AbstractContainerMenu {
    public LaunchMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv);
    }

    public LaunchMenu(int containerId, Inventory inv) {
        super(utmMenus.LAUNCH_MENU.get(), containerId);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.NULL, player, utmBlocks.LAUNCH_CONTRAPTION.block.get());
    }
}
