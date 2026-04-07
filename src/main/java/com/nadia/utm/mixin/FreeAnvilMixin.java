package com.nadia.utm.mixin;

import com.nadia.utm.Config;
import com.nadia.utm.registry.block.utmBlocks;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("SameReturnValue")
@Mixin(value = AnvilMenu.class, remap = false)
public abstract class FreeAnvilMixin extends ItemCombinerMenu {
    public FreeAnvilMixin(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }

    @ModifyConstant(method = "mayPickup", constant = @Constant(intValue = 0))
    private int utm$allowFreeAnvilCraft(int constant) {
        ItemStack outputStack = this.resultSlots.getItem(0);

        if (!outputStack.isEmpty() && outputStack.is(utmBlocks.HEAVY_METAL_ANVIL.ITEM.get()) && Config.HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT.getAsInt() <= 0) {
            return -1;
        }

        return 0;
    }

    @Redirect(
            method = "createResult",
            at= @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z", opcode = Opcodes.GETFIELD, ordinal = 1)
    )
    public boolean utm$overrideTooExpensive(Abilities instance) {
        return true;
    }
}