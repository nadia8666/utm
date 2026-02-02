package com.nadia.utm.item;

import com.nadia.utm.utm;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;


public class FiddleheadItem extends Item {
    public FiddleheadItem(Properties properties) {
        super(properties);
    }
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        utm.LOGGER.info("[UTM] got");
        return UseAnim.SPEAR;
    }
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {

        utm.LOGGER.info("[UTM] used");
        ItemStack itemstack = player.getItemInHand(hand);
        if (!isTooDamagedToUse(itemstack)) {
            var hi = player.getLookAngle();
            var mult = 0.5;
            if (player.isFallFlying() & (player.position().y > 512)) {
              mult=10;
            }
            //  player.startUsingItem(hand);
            player.setDeltaMovement(hi.x * mult, hi.y * Math.min(mult,1), hi.z * mult);
            itemstack.setDamageValue(1);
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }
    private static boolean isTooDamagedToUse(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
        //idk how to add damage to items : )
    }

    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {

        return true; // yes
    }

}
