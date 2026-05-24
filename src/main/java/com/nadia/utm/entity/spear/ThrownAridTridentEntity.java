package com.nadia.utm.entity.spear;

import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class ThrownAridTridentEntity extends AbstractArrow {
    public float LAST_ROTATION = 0;
    public boolean HIT_ENTITY = false;
    public ItemStack COPY_STACK;

    private static final EntityDataAccessor<String> MODEL =
            SynchedEntityData.defineId(ThrownAridTridentEntity.class, EntityDataSerializers.STRING);

    public ThrownAridTridentEntity(Level level) {
        super(utmEntities.THROWN_SPEAR.get(), level);
        COPY_STACK = new ItemStack(utmTools.COPPER_THROWING_SPEAR.get());
    }

    public ThrownAridTridentEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(utmEntities.THROWN_SPEAR.get(), shooter, level, stack, stack);
        COPY_STACK = stack.copy();
        updateModel();
    }

    public ThrownAridTridentEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(utmEntities.THROWN_SPEAR.get(), x, y, z, level, stack, stack);
        COPY_STACK = stack.copy();
        updateModel();
    }

    public void updateModel() {
        if (level().isClientSide) return;

        entityData.set(MODEL, COPY_STACK.getOrDefault(utmDataComponents.THROWING_SPEAR_MODEL, "copper_throwing_spear"));
    }

    public String getModel() {
        return entityData.get(MODEL);
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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MODEL, "default");
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
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("SpearModel", getModel());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(MODEL, compound.getString("SpearModel"));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        HIT_ENTITY = true;

        float[] damage = {0.0F};

        ItemAttributeModifiers modifiers = COPY_STACK.getOrDefault(
                net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY
        );

        modifiers.forEach(EquipmentSlotGroup.MAINHAND, (attributeHolder, modifier) -> {
            if (attributeHolder != null && attributeHolder.unwrap().left().isPresent() && Attributes.ATTACK_DAMAGE.getKey() != null) {
                if (attributeHolder.is(Attributes.ATTACK_DAMAGE.getKey())) {
                    damage[0] += (float) modifier.amount();
                }
            }
        });

        Entity entity = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().fallingStalactite(entity);

        if (entity.hurt(source, damage[0]))
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

                itemStack.hurtAndBreak(canBreak ? 5 : Math.min(5, itemStack.getMaxDamage() - itemStack.getDamageValue() - 1), serverLevel, null, item -> {
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
