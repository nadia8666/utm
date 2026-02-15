package com.nadia.utm.item;

import com.nadia.utm.registry.data.utmDataComponents;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.schematics.SchematicItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.NotNull;

public class LockedSchematicItem extends SchematicItem {
    public final String SCHEMATIC;

    public LockedSchematicItem(String schematic) {
        super(new Item.Properties().stacksTo(1));

        this.SCHEMATIC = schematic;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.set(utmDataComponents.IS_PLACABLE_SCHEMATIC, true);
        stack.set(AllDataComponents.SCHEMATIC_FILE, this.SCHEMATIC);
        return stack;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide && !stack.has(AllDataComponents.SCHEMATIC_FILE)) {
            setupDefaultComponents(stack, level);
        }
    }

    private void setupDefaultComponents(ItemStack stack, Level level) {
        stack.set(AllDataComponents.SCHEMATIC_DEPLOYED, false);
        stack.set(AllDataComponents.SCHEMATIC_OWNER, "SERVER");
        stack.set(AllDataComponents.SCHEMATIC_ANCHOR, BlockPos.ZERO);
        stack.set(AllDataComponents.SCHEMATIC_ROTATION, Rotation.NONE);
        stack.set(AllDataComponents.SCHEMATIC_MIRROR, Mirror.NONE);
        stack.set(utmDataComponents.IS_PLACABLE_SCHEMATIC, true);
        stack.set(AllDataComponents.SCHEMATIC_FILE, this.SCHEMATIC);

        SchematicItem.writeSize(level, stack);
    }
}