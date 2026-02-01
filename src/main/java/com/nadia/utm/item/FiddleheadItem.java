package com.nadia.utm.item;

import com.nadia.utm.utm;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FiddleheadItem extends Item {
    public FiddleheadItem(Properties properties) {
        super(properties);
    }
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        utm.LOGGER.info("[UTM] got");
        return UseAnim.SPEAR;
    }
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        utm.LOGGER.info("[UTM] used");
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isFallFlying() & (player.position().y >300)) {
            var hi  = player.getLookAngle();
            var mult = 10;
            player.setDeltaMovement(hi.x * mult, hi.y*1, hi.z*mult);
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
          //  player.startUsingItem(hand);

    }
    private static boolean isTooDamagedToUse(ItemStack stack) {
        return true; // stack.getDamageValue() >= stack.getMaxDamage() - 1;
        //idk how to add damage to items : )
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {

        return true; // yes
    }

}
