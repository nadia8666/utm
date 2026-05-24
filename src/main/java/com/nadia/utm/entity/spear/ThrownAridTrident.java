package com.nadia.utm.entity.spear;

import com.nadia.utm.registry.entity.utmEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ThrownAridTrident extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownAridTrident.class, EntityDataSerializers.BOOLEAN);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;
    public ThrownAridTrident(EntityType<? extends ThrownTrident> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownAridTrident(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
        super(EntityType.TRIDENT, shooter, level, pickupItemStack, (ItemStack)null);
        this.entityData.set(ID_FOIL, pickupItemStack.hasFoil());
    }

    public ThrownAridTrident(Level level, double x, double y, double z, ItemStack pickupItemStack) {
        super(EntityType.TRIDENT, x, y, z, level, pickupItemStack, pickupItemStack);
        this.entityData.set(ID_FOIL, pickupItemStack.hasFoil());
    }

    public ThrownAridTrident(Level level) {
        super(utmEntities.THROWN_ARID_TRIDENT.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_FOIL, false);
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();

        float damage = 999.0F;
        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, owner == null ? this : owner);

        if (entity.hurt(damagesource, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (level() instanceof ServerLevel serverlevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel, entity, damagesource, this.getWeaponItem());
            }

            if (entity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, damagesource);
                this.doPostHurtEffects(livingentity);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }


    // from trident
    public void tick() {
        if (this.inGroundTime > 1) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();


        super.tick();
    }
    boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();
        return entity != null && entity.isAlive() ? !(entity instanceof ServerPlayer) || !entity.isSpectator() : false;
    }
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }
    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.TRIDENT);
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public void playerTouch(Player entity) {
        if (this.ownedBy(entity) || this.getOwner() == null) {
            super.playerTouch(entity);
        }

    }
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.dealtDamage = compound.getBoolean("DealtDamage");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    private byte getLoyaltyFromItem(ItemStack stack) {
        Level var3 = this.level();
        byte var10000;
        if (var3 instanceof ServerLevel serverlevel) {
            var10000 = (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverlevel, stack, this), 0, 127);
        } else {
            var10000 = 0;
        }

        return var10000;
    }

    public void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED)  {
            super.tickDespawn();
        }

    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
