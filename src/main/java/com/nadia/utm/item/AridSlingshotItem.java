package com.nadia.utm.item;

import com.nadia.utm.entity.projectile.AridStoneEntity;
import com.nadia.utm.util.EnchantUtil;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AridSlingshotItem extends Item {
    public AridSlingshotItem(Properties properties) {
        super(properties);
    }
    //https://github.com/nadia8666/bee-mod/blob/master/src/main/java/com/ihatebees/item/custom/weapon/GummyballerSwordItem.java


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        int multishot = itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.MULTISHOT));
        int power = itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.POWER));;


        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, player.getSoundSource(), 0.5f, 0.8f);
        player.getCooldowns().addCooldown(this,5+(multishot*3)+power);
        if (!level.isClientSide) {
            AridStoneEntity gummyballentity = new AridStoneEntity(level, player);

            gummyballentity.shootFromRotation(player,  player.getXRot(), player.getYRot(), 0.0f, 1.5f+(power*0.4f), 0f);
            level.addFreshEntity(gummyballentity);
        }
        if (multishot>=1) {
            for (int i =1; i <= multishot; i++ ) {
                for (int u = 1; u<=2; u++) { //DOUBLE FOR LOOPS :O
                    AridStoneEntity gummyballentity = new AridStoneEntity(level, player);
                    gummyballentity.shootFromRotation(player, player.getXRot(), (float) (player.getYRot()-((4.5f/multishot)*i*(Math.pow(-1,u)))), 0.0f, 1.5f+(power*(0.4f)*((float) 1 /i)), (float) i /2);
                    level.addFreshEntity(gummyballentity);
                }
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().invulnerable) { //formerly creative mode..
            itemStack.hurtAndBreak(1+(multishot==0 ? 0 : (multishot*2)), player, EquipmentSlot.MAINHAND);
            //so THIS is how hurtAndBreak works..
        }
        return InteractionResultHolder.success(itemStack);    }

    @Override
    public boolean isBookEnchantable(@NotNull ItemStack stack, @NotNull ItemStack book) {
        if (EnchantUtil.findEnchants(stack, book, Enchantments.MENDING))
            return false; // No mening

        return super.isBookEnchantable(stack, book);
    }
}
