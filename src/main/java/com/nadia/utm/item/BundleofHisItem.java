package com.nadia.utm.item;

import com.nadia.utm.entity.projectile.AridStoneEntity;
import com.nadia.utm.entity.projectile.PebbleEntity;
import com.nadia.utm.util.EnchantUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BundleofHisItem extends Item {
    public BundleofHisItem(Properties properties) {
        super(properties);
    }
    //https://github.com/nadia8666/bee-mod/blob/master/src/main/java/com/ihatebees/item/custom/weapon/GummyballerSwordItem.java


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, player.getSoundSource(), 0.65f, 0.75f);
        if (!level.isClientSide) {
            for (int i = 1; i<32; i++) {
                PebbleEntity gummyballentity = new PebbleEntity(level, player);

                gummyballentity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 0.4f, 30f);
                level.addFreshEntity(gummyballentity);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().invulnerable) { //formerly creative mode..
            itemStack.consume(1, player);
            //so THIS is how hurtAndBreak works..
        }
        return InteractionResultHolder.success(itemStack);    }

}
