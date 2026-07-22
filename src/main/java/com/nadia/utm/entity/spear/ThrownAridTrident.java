package com.nadia.utm.entity.spear;

import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.client.particle.Particle;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ThrownAridTrident extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownAridTrident.class, EntityDataSerializers.BOOLEAN);
    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;
    public ItemStack COPIED_STACK;
    public Particle larpicle;


    public ThrownAridTrident(Level level) {
        super(utmEntities.THROWN_ARID_TRIDENT.get(), level);
        COPIED_STACK = new ItemStack(utmTools.ARID_TRIDENT.get());
       }

    public ThrownAridTrident(Level level, LivingEntity shooter, ItemStack stack) {
        super(utmEntities.THROWN_ARID_TRIDENT.get(), shooter, level, stack, stack);
        this.entityData.set(ID_FOIL, stack.hasFoil());
        COPIED_STACK = stack.copy();
    }

    public ThrownAridTrident(Level level, double x, double y, double z, ItemStack stack) {
        super(utmEntities.THROWN_ARID_TRIDENT.get(), x, y, z, level, stack, stack);
        this.entityData.set(ID_FOIL, stack.hasFoil());
        COPIED_STACK = stack.copy();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_FOIL, false);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();

        float damage = 40.0F; // considerable.. ok so the axe doesa lot of damage and is really eay to use. This is harder to use than axe
        // so more damage to counter act that?
        // nadia: if it can be enchanted probably no
        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, owner == null ? this : owner);

        if (entity.hurt(damagesource, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (level() instanceof ServerLevel serverlevel) {
                //could be useful in da future for sword particles
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel, entity, damagesource, this.getWeaponItem());
            }

            if (entity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, damagesource);
                this.doPostHurtEffects(livingentity);
            }
        }

        //Hit Stuff Here!
        int bp = COPIED_STACK.getEnchantmentLevel(this.registryAccess().holderOrThrow(utmEnchantments.POWER_JUMP));
        int piercing = COPIED_STACK.getEnchantmentLevel(this.registryAccess().holderOrThrow(Enchantments.PIERCING));

        if (bp>0) {
            this.playSound(SoundEvents.TRIDENT_RETURN, 1.0F, 1.0F);
            float g = ((float) (Math.pow(bp,1.15)) /12);
            this.setDeltaMovement(this.getDeltaMovement().multiply(-g, -g, -g).add(0,1.5-(0.5*bp),0));

        } else if (piercing>0) {
            float g = ((float) piercing /5);

            this.setDeltaMovement(this.getDeltaMovement().multiply(g,g,g));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        }
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }


    // from trident
    @Override
    public void tick() {
        super.tick();

        if (this.inGroundTime > 1)
            this.dealtDamage = true;
    }

    boolean isAcceptibleReturnOwner() {
        Entity entity = this.getOwner();
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayer) || !entity.isSpectator());
    }

    @Nullable
    protected EntityHitResult findHitEntity(@NotNull Vec3 startVec, @NotNull Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    public @NotNull ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    protected boolean tryPickup(@NotNull Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(utmTools.ARID_TRIDENT.get());
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(@NotNull Player entity) {
        if (this.ownedBy(entity) || this.getOwner() == null)
            super.playerTouch(entity);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.dealtDamage = compound.getBoolean("DealtDamage");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    private byte getLoyaltyFromItem(ItemStack stack) {
        Level level = this.level();
        byte acceleration;
        if (level instanceof ServerLevel serverlevel) {
            acceleration = (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverlevel, stack, this), 0, 127);
        } else {
            acceleration = 0;
        }

        return acceleration;
    }

    @Override
    public void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED)
            super.tickDespawn();
    }

    @Override
    protected float getWaterInertia() {
        return 0.2F;
    } // water inertia

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
