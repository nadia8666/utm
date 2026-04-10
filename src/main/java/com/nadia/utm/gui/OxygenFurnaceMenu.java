package com.nadia.utm.gui;

import com.nadia.utm.block.entity.OxygenFurnaceBlockEntity;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.ui.utmMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class OxygenFurnaceMenu extends AbstractContainerMenu {
    public final OxygenFurnaceBlockEntity blockEntity;
    private final Level level;
    public final ContainerData data;

    public OxygenFurnaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public OxygenFurnaceMenu(int containerId, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(utmMenus.OXYGEN_FURNACE_MENU.get(), containerId);

        this.blockEntity = (OxygenFurnaceBlockEntity) blockEntity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(this.blockEntity.INVENTORY, 0, 34, 24) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.IRON_INGOT);
            }
        });
        this.addSlot(new SlotItemHandler(this.blockEntity.INVENTORY, 1, 34, 46) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(ItemTags.COALS);
            }
        });

        this.addDataSlots(data);
    }

    // slots 36+ = TileInventory slots
    public static final int IRON_SLOT_INDEX = 36;
    public static final int FUEL_SLOT_INDEX = 37;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // item in , item out

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, IRON_SLOT_INDEX, IRON_SLOT_INDEX + 1, false) && !moveItemStackTo(sourceStack, FUEL_SLOT_INDEX, FUEL_SLOT_INDEX + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, utmBlocks.OXYGEN_FURNACE.BLOCK.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    public double getUIProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return (double) (maxProgress - progress) /maxProgress;
    }
}
