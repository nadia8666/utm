package com.nadia.utm.item;

import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
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
        if (level.isClientSide()) return InteractionResultHolder.consume(itemstack);

        if (!isTooDamagedToUse(itemstack)) {
            Vec3 angle = player.getLookAngle();
            double mult = level.dimension().equals(utmDimensions.SPACE_KEY) ? 0.5 : player.isFallFlying() & (player.position().y > 512) ? 20 : 0.5;

            Vec3 speed = player.getDeltaMovement();
            Vec3 add = new Vec3(angle.x * mult, angle.y * Math.min(mult, 1), angle.z * mult);
            Vec3 newSpeed = speed.add(add);

            player.setDeltaMovement(newSpeed.x, newSpeed.y, newSpeed.z);
            itemstack.setDamageValue(itemstack.getDamageValue() + 1);

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
