package com.nadia.utm.entity.spear;

import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ThrownAridTrident extends ThrownTrident {
    private static final EntityDataAccessor<Byte> ID_LOYALTY = null;
    private static final EntityDataAccessor<Boolean> ID_FOIL = null;
    private boolean dealtDamage;
    public ItemStack COPY_STACK;
    public String getModel() {
        return entityData.get(MODEL);
    }
    private static final EntityDataAccessor<String> MODEL =
            SynchedEntityData.defineId(ThrownAridTrident.class, EntityDataSerializers.STRING);


    public int clientSideReturnTridentTickCount;
    public ThrownAridTrident(EntityType<? extends ThrownTrident> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownAridTrident(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
        super(level, shooter, pickupItemStack);
        this.entityData.set(ID_FOIL, pickupItemStack.hasFoil());
    }
    public ThrownAridTrident(Level level) {
        super(utmEntities.THROWN_ARID_TRIDENT.get(), level);
        COPY_STACK = new ItemStack(utmTools.ARID_TRIDENT.get());
    }
    public void updateModel() {
        if (level().isClientSide) return;

        entityData.set(MODEL, COPY_STACK.getOrDefault(utmDataComponents.THROWING_SPEAR_MODEL, "copper_throwing_spear"));
    }

    public ThrownAridTrident(Level level, double x, double y, double z, ItemStack pickupItemStack) {
        super(level, x, y, z, pickupItemStack);
        this.entityData.set(ID_FOIL, pickupItemStack.hasFoil());
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LOYALTY, (byte)0);
        builder.define(ID_FOIL, false);
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 999.0F;
        Entity entity1 = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, (Entity)(entity1 == null ? this : entity1));
        Level var7 = this.level();
        if (var7 instanceof ServerLevel serverlevel) {
        }

        this.dealtDamage = true;
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            var7 = this.level();
            if (var7 instanceof ServerLevel) {
                ServerLevel serverlevel1 = (ServerLevel)var7;
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, entity, damagesource, this.getWeaponItem());
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;
                this.doKnockback(livingentity, damagesource);
                this.doPostHurtEffects(livingentity);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

}
