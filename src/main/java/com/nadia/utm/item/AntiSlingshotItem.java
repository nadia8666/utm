package com.nadia.utm.item;

import com.nadia.utm.entity.projectile.PebbleEntity;
import com.nadia.utm.registry.sound.utmSounds;
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

// dont hink about this class - nadia
public class AntiSlingshotItem extends Item {
    public AntiSlingshotItem(Properties properties) {
        super(properties);
    }
    //https://github.com/nadia8666/bee-mod/blob/master/src/main/java/com/ihatebees/item/custom/weapon/GummyballerSwordItem.java


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        int multishot = 1+ itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.MULTISHOT));
        int power = itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.POWER));;
        int flame = itemStack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.FLAME));;

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, player.getSoundSource(), 1f, 0.2f);
        if (flame>0) level.playSound(null, player.getX(), player.getY(), player.getZ(), utmSounds.PKFRS2, player.getSoundSource(), 0.5f, 0.7f);
        //make it use an utm mother 2 pkfrs sound

        if (multishot>1) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, player.getSoundSource(), 0.7f, 0.6f);
        }
        player.getCooldowns().addCooldown(this,10+((multishot-1)*30)+flame*5); //5 + 3 + 1
        if (!level.isClientSide) {
            PebbleEntity gummyballentity = new PebbleEntity(level, player);
            gummyballentity.setRemainingFireTicks(flame*200);

            gummyballentity.shootFromRotation(player,  player.getXRot(), player.getYRot(), 0.0f, 1f+(power*0.5f), 0f);
            level.addFreshEntity(gummyballentity);
        }
        if (multishot>=1) {
            boolean sneaking = player.isCrouching();
            boolean jumping = !player.onGround();
            for (int i =1; i <= (multishot-1)*8 + 1; i++ ) {
                for (int T = 1; T<=2; T++) { //DOUBLE FOR LOOPS :O
                    for (int u = 1; u <= 2; u++) { //TRIPE FOR LOOPS :O
                        PebbleEntity gummyballentity = new PebbleEntity(level, player);

                        gummyballentity.setRemainingFireTicks(flame*200);

                        double calculationy = ((power + 3.5f+(i/8)) * (sneaking ? 11f / 5 : 1) * i * (Math.pow(-1, T)));
                        double calculationx = ((power + 3.5f+(i/8)) * (sneaking ? 3f / 5 : 1) * i * (Math.pow(-1, u)));

                        gummyballentity.shootFromRotation(player,
                                (float) (player.getXRot() - (jumping ? calculationy : calculationx)),
                                (float) (player.getYRot() - (!jumping ? calculationy : calculationx)),
                                0.0f, 1f + (power * (0.3f)), (float) Math.pow((i-1),1));
                        level.addFreshEntity(gummyballentity);
                    }
                }
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().invulnerable) { //formerly creative mode..
            itemStack.hurtAndBreak(1+(multishot==0 ? 0 : (multishot-1)*(49+flame*50))+flame, player, EquipmentSlot.MAINHAND);
            //so THIS is how hurtAndBreak works..
        }
        return InteractionResultHolder.success(itemStack);    }

}
