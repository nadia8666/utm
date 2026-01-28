package com.nadia.utm.mixin;

import com.nadia.utm.Config;
import com.nadia.utm.utm;
import com.nadia.utm.utmRegister;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = AnvilMenu.class, remap = false)
public abstract class FreeAnvilMixin extends ItemCombinerMenu {
    public FreeAnvilMixin(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }

    @ModifyConstant(method = "mayPickup", constant = @Constant(intValue = 0))
    private int utm$allowFreeAnvilCraft(int constant) {
        utm.LOGGER.info("[UTM] Attempting anvil bypass");
        ItemStack outputStack = this.resultSlots.getItem(0);

        if (!outputStack.isEmpty() && outputStack.is(utmRegister.HEAVY_METAL_ANVIL.item.get()) && Config.HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT.getAsInt() <= 0) {
            utm.LOGGER.info("[UTM] Anvil success");
            return -1;
        }

        return 0;
    }
}