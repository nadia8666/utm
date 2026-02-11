package com.nadia.utm.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;


public class FiddleheadItem extends Item {
    public FiddleheadItem(Properties properties) {
        super(properties);
    }
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPEAR;
    }
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!isTooDamagedToUse(itemstack)) {
            var hi = player.getLookAngle();
            var mult = 0.5;
            if (player.isFallFlying() & (player.position().y > 512)) {
              mult=20;
            }
            //  player.startUsingItem(hand);
            var hi2 = player.getDeltaMovement();
            var hi3 = new Vec3(hi.x * mult, hi.y * Math.min(mult,1), hi.z * mult);
            var hi4 = hi2.add(hi3);
            player.setDeltaMovement(hi4.x,hi4.y,hi4.z);
            itemstack.setDamageValue(itemstack.getDamageValue()+1);
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
