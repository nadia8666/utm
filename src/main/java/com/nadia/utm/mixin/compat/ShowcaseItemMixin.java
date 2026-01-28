package com.nadia.utm.mixin.compat;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.nadia.utm.compat.ShareJeiItem;
import com.nadia.utm.utm;
import com.ultramega.showcaseitem.ShowcaseItemFeature;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShowcaseItemFeature.class, remap = false)
public class ShowcaseItemMixin {
    @Definition(id = "getSlotUnderMouse", method = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getSlotUnderMouse()Lnet/minecraft/world/inventory/Slot;")
    @Expression("? = ?.getSlotUnderMouse()")
    @Inject(
            method = "keyPressed",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
            cancellable = true)
    private static void utm$showcaseCompat(CallbackInfo ci) {
        utm.LOGGER.info("[UTM] yea");

        Minecraft mc = Minecraft.getInstance();
        double mouseX = mc.mouseHandler.xpos() * (double)mc.getWindow().getGuiScaledWidth() / (double)mc.getWindow().getWidth();
        double mouseY = mc.mouseHandler.ypos() * (double)mc.getWindow().getGuiScaledHeight() / (double)mc.getWindow().getHeight();

        EmiIngredient hovered = EmiApi.getHoveredStack((int)mouseX, (int)mouseY, false).getStack();

        if (!hovered.isEmpty()) {
            EmiStack emiStack = hovered.getEmiStacks().get(0);
            ItemStack target = emiStack.getItemStack();

            if (!target.isEmpty()) {
                String itemName = target.getDisplayName().getString();
                PacketDistributor.sendToServer(new ShareJeiItem(itemName));

                // Cancel the original mouse event
                ci.cancel();
            }
        }
    }

    @Inject(method = "shareItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private static void utm$testCompat(ServerPlayer player, int slotIndex, int containerId, CallbackInfo ci) {
        ItemStack stack;
        NonNullList<Slot> slots = player.containerMenu.slots;
        if (player.containerMenu instanceof InventoryMenu) {
            stack = player.getInventory().getItem(slotIndex);
        } else {
            Slot slot = (Slot)slots.get(slotIndex);
            stack = slot.getItem();
        }

        utm.LOGGER.info("[UTM] ok: {}", stack.getDisplayName());
    }
}
