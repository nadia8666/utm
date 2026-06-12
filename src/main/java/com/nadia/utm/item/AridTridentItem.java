package com.nadia.utm.item;

import com.nadia.utm.entity.spear.ThrownAridTrident;
import com.nadia.utm.registry.particle.utmParticles;
import com.nadia.utm.registry.sound.utmSounds;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AridTridentItem extends TridentItem {
    public static final int THROW_THRESHOLD_TIME = 20;
    public static final float BASE_DAMAGE = -1.0F;
    public static final float SHOOT_POWER = 50F;

    public AridTridentItem(Properties properties) {
        super(properties);
    }

    //there is nothing you nee to change about this file
    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, Position pos, ItemStack stack, @NotNull Direction direction) {
        ThrownAridTrident throwntrident = new ThrownAridTrident(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1)); //OVERWRITE THROWNTRIDENT
        throwntrident.pickup = AbstractArrow.Pickup.ALLOWED;
        return throwntrident;
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 3.0F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND //less damage bcz its meant to eb a THrowing Weapon and not a hitting one
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    private static boolean isTooDamagedToUse(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int i = this.getUseDuration(stack, entityLiving) - timeLeft;
            if (i >= 20) {
                Holder<SoundEvent> holder = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
                if (level instanceof ServerLevel serverLevel) { // modified from level.isclientsicde
                    stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));

                    ThrownAridTrident trident = new ThrownAridTrident(serverLevel, player, stack);
                    trident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5F, 0F);
                    if (player.hasInfiniteMaterials())
                        trident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    else
                        player.getInventory().removeItem(stack);

                    serverLevel.addFreshEntity(trident);
                    serverLevel.playSound(null, trident, holder.value(), SoundSource.PLAYERS, 0.8F, 0.8F);
                    serverLevel.playSound(null, trident, utmSounds.PKFRS.get(), SoundSource.PLAYERS, 0.7F, 1.0F);
                    serverLevel.sendParticles(utmParticles.PKFIREBEGIN.get(), trident.position().x + player.getLookAngle().x, trident.position().y + player.getLookAngle().y, trident.position().z + player.getLookAngle().z, 1, 0, 0, 0, 0); //so i can send parite
                }

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isTooDamagedToUse(itemstack)) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }


}