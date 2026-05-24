package com.nadia.utm.item.spear;

import com.nadia.utm.entity.spear.ThrownSpearEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ThrowingSpearItem extends Item implements ProjectileItem {
    private final float attackDamage;
    private final float attackSpeed;

    public ThrowingSpearItem(Properties properties, float attackDamage, float attackSpeed) {
        super(properties);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    @Override
    public @NotNull ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .positionFunction(DispenseConfig.DEFAULT.positionFunction())
                .uncertainty(0F)
                .power(3.125F)
                .build();
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, this.attackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, this.attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeCharged) {
        int chargeTime = this.getUseDuration(stack, entity) - timeCharged;
        if (!level.isClientSide && chargeTime >= 5 && entity instanceof Player player) {
            ThrownSpearEntity spear = new ThrownSpearEntity(level, player, stack);
            spear.pickup = AbstractArrow.Pickup.ALLOWED;
            spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.125F, 0F);
            level.addFreshEntity(spear);

            Holder<SoundEvent> holder = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND)
                    .orElse(SoundEvents.TRIDENT_THROW);
            level.playSound(null, spear, holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

            if (stack.getMaxStackSize() == 1)
                player.getInventory().removeItem(stack);
            else
                stack.shrink(1);
        }
    }

    @Override
    public boolean useOnRelease(@NotNull ItemStack stack) {
        return super.useOnRelease(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isTooDamagedToUse(itemstack)) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    private static boolean isTooDamagedToUse(ItemStack stack) {
        return stack.getMaxDamage() != 0 && stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return true;
    }

    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, Position pos, ItemStack stack, @NotNull Direction direction) {
        ThrownSpearEntity spear = new ThrownSpearEntity(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
        spear.pickup = AbstractArrow.Pickup.ALLOWED;

        return spear;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, net.neoforged.neoforge.common.@NotNull ItemAbility itemAbility) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_TRIDENT_ACTIONS.contains(itemAbility);
    }

    public void hitEnemy(ThrownSpearEntity entity) {

    }

    public void hitBlock(ThrownSpearEntity entity) {

    }
}
