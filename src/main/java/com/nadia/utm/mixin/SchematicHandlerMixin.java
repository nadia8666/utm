package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.registry.data.utmDataComponents;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.schematics.client.SchematicHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SchematicHandler.class, remap = false)
public abstract class SchematicHandlerMixin {
    @Accessor("activeHotbarSlot")
    public abstract void utm$setActiveHotbarSlot(int slot);

    @Accessor("activeSchematicItem")
    public abstract void utm$setactiveSchematicItem(ItemStack slot);

    @Accessor("activeSchematicItem")
    public abstract ItemStack activeSchematicItem();

    @Accessor("activeHotbarSlot")
    public abstract int activeHotbarSlot();

    /**
     * @author nadiarr
     * @reason required
     */
    @Overwrite
    private ItemStack findBlueprintInHand(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!stack.has(AllDataComponents.SCHEMATIC_FILE))
            return null;

        utm$setactiveSchematicItem(stack);
        utm$setActiveHotbarSlot(player.getInventory().selected);
        return stack;
    }

    @Redirect(
            method = "init",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z")
    )
    private boolean utm$bypass(LocalPlayer player, @Local(argsOnly = true) ItemStack stack) {
        return player.isCreative() || stack.has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get());
    }

    @Redirect(
            method = "deploy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z")
    )
    private boolean utm$bypass(LocalPlayer player) {
        return player.isCreative() || player.getMainHandItem().has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get()) || player.getOffhandItem().has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get());
    }

    @Inject(method = "printInstantly", at = @At("TAIL"))
    private void utm$destroy(CallbackInfo ci) {
        if (activeSchematicItem() != null && activeSchematicItem().has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get())) {
            activeSchematicItem().setCount(0);
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.getInventory().setItem(activeHotbarSlot(), ItemStack.EMPTY);
        }
    }
}