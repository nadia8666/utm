package com.nadia.utm.entity.spear;

import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class ThrownSpearEntity extends AbstractArrow {
    public float LAST_ROTATION = 0;
    public boolean HIT_ENTITY = false;

    public ThrownSpearEntity(Level level) {
        super(utmEntities.THROWN_SPEAR.get(), level);
    }

    public ThrownSpearEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(utmEntities.THROWN_SPEAR.get(), shooter, level, stack, stack);
    }

    public ThrownSpearEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(utmEntities.THROWN_SPEAR.get(), x, y, z, level, stack, stack);
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(utmTools.COPPER_THROWING_SPEAR.get());
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    protected float getWaterInertia() {
        return 0.8F;
    }

    @Override
    public void tickDespawn() {
    }

    public boolean isFlying() {
        return this.getVehicle() == null && !this.inGround;
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        HIT_ENTITY = true;

        Entity entity = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().fallingStalactite(entity);

        if (entity.hurt(source, 5.5F))
            if (entity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, source);
                this.doPostHurtEffects(livingentity);
            }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);

        if (HIT_ENTITY) return;

        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            ItemStack itemStack = this.getPickupItem();
            this.playSound(SoundEvents.BELL_BLOCK, 1.0F, 1.0F);

            if (!itemStack.isEmpty()) {
                boolean canBreak = itemStack.getEnchantmentLevel(this.registryAccess().holderOrThrow(Enchantments.VANISHING_CURSE)) > 0;

                itemStack.hurtAndBreak(canBreak ? 5 : Math.min(5, itemStack.getMaxDamage()-itemStack.getDamageValue()-1), serverLevel, null, item -> {
                    this.discard();
                    this.playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F);
                });

                if (!this.isRemoved())
                    this.setPickupItemStack(itemStack);
            }
        }
    }

    @Override
    protected boolean tryPickup(@NotNull Player player) {
        return this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }
}
