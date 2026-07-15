package com.nadia.utm.item;

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

public class StoneSlingshotItem extends Item {
    public StoneSlingshotItem(Properties properties) {
        super(properties);
    }
    //https://github.com/nadia8666/bee-mod/blob/master/src/main/java/com/ihatebees/item/custom/weapon/GummyballerSwordItem.java


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        int multishot = itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.MULTISHOT));
        int power = itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.POWER));;
        int flame = itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.FLAME));;


        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, player.getSoundSource(), 0.5f, 0.8f);
        if (flame>0) level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, player.getSoundSource(), 0.3f, 0.8f);

        player.getCooldowns().addCooldown(this,3+(multishot*2)+power+(flame*2)); //5 + 3 + 1
        if (!level.isClientSide) {
            PebbleEntity gummyballentity = new PebbleEntity(level, player);
            gummyballentity.setRemainingFireTicks(flame*200);

            gummyballentity.shootFromRotation(player,  player.getXRot(), player.getYRot(), 0.0f, 0.8f+(power*0.5f), 1f);
            level.addFreshEntity(gummyballentity);
        }
        if (multishot>=1) {
            boolean sneaking = player.isCrouching();
            boolean jumping = !player.onGround();
            for (int i =1; i <= multishot; i++ ) {
                for (int u = 1; u<=2; u++) { //DOUBLE FOR LOOPS :O
                    PebbleEntity gummyballentity = new PebbleEntity(level, player);

                    double calculation = ((10f/multishot)*(sneaking ? 3f/5 : 1)*i*(Math.pow(-1,u)));

                    gummyballentity.shootFromRotation( player, (float) (player.getXRot()-(jumping ? calculation : 0)),
                            (float) (player.getYRot()-(!jumping ? calculation : 0)),
                            0.0f, 0.7f+(power*(0.4f)*((float) 1 /i)), (float) i*4);
                    gummyballentity.setRemainingFireTicks(flame*200);
                    level.addFreshEntity(gummyballentity);
                }
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().invulnerable) { //formerly creative mode..
            itemStack.hurtAndBreak(1+(multishot==0 ? 0 : (multishot*2))+flame, player, EquipmentSlot.MAINHAND);
            //so THIS is how hurtAndBreak works..
        }
        return InteractionResultHolder.success(itemStack);    }

}
