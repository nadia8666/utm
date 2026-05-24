package com.nadia.utm.entity.spear;

import com.nadia.utm.item.spear.ThrowingSpearItem;
import com.nadia.utm.registry.codec.utmCodecs;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.Objects;

import static com.nadia.utm.client.renderer.glint.utmGlintContainer.DEFAULT_COLOR;
import static com.nadia.utm.client.renderer.glint.utmGlintContainer.GLINT_DEFAULT;

public class ThrownSpearEntity extends AbstractArrow {
    public float LAST_ROTATION = 0;
    public boolean HIT_ENTITY = false;
    public long HIT_TIME = -1;
    public ItemStack COPY_STACK;

    private static final EntityDataAccessor<String> MODEL =
            SynchedEntityData.defineId(ThrownSpearEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> FOIL =
            SynchedEntityData.defineId(ThrownSpearEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> RECOVER =
            SynchedEntityData.defineId(ThrownSpearEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> GLINT_COLOR =
            SynchedEntityData.defineId(ThrownSpearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> GLINT_LOCATION =
            SynchedEntityData.defineId(ThrownSpearEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Vector2f> GLINT_SPEED =
            SynchedEntityData.defineId(ThrownSpearEntity.class, utmCodecs.ENTITY_VECTOR2F.get());
    private static final EntityDataAccessor<Vector2f> GLINT_SCALE =
            SynchedEntityData.defineId(ThrownSpearEntity.class, utmCodecs.ENTITY_VECTOR2F.get());
    private static final EntityDataAccessor<Boolean> GLINT_ADDITIVE =
            SynchedEntityData.defineId(ThrownSpearEntity.class, EntityDataSerializers.BOOLEAN);

    public ThrownSpearEntity(Level level) {
        super(utmEntities.THROWN_SPEAR.get(), level);
        COPY_STACK = new ItemStack(utmTools.COPPER_THROWING_SPEAR.get());
    }

    public ThrownSpearEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(utmEntities.THROWN_SPEAR.get(), shooter, level, stack, stack);
        COPY_STACK = stack.copy();
        updateModel();
    }

    public ThrownSpearEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(utmEntities.THROWN_SPEAR.get(), x, y, z, level, stack, stack);
        COPY_STACK = stack.copy();
        updateModel();
    }

    public void updateModel() {
        if (level().isClientSide) return;

        entityData.set(MODEL, COPY_STACK.getOrDefault(utmDataComponents.THROWING_SPEAR_MODEL, "copper_throwing_spear"));
        entityData.set(FOIL, COPY_STACK.hasFoil());
        entityData.set(RECOVER, COPY_STACK.getEnchantmentLevel(registryAccess().holderOrThrow(utmEnchantments.SPEAR_RECOVERY)) > 0);

        entityData.set(GLINT_COLOR, COPY_STACK.getOrDefault(utmDataComponents.GLINT_COLOR, DEFAULT_COLOR));
        entityData.set(GLINT_LOCATION, COPY_STACK.getOrDefault(utmDataComponents.GLINT_TYPE, GLINT_DEFAULT).toString());
        entityData.set(GLINT_SPEED, COPY_STACK.getOrDefault(utmDataComponents.GLINT_SPEED, new Vector2f(1, 1)));
        entityData.set(GLINT_SCALE, COPY_STACK.getOrDefault(utmDataComponents.GLINT_SCALE, new Vector2f(1, 1)));
        entityData.set(GLINT_ADDITIVE, COPY_STACK.getOrDefault(utmDataComponents.GLINT_ADDITIVE, true));
    }

    public String getModel() {
        return entityData.get(MODEL);
    }

    public Boolean getFoil() {
        return entityData.get(FOIL);
    }

    public Boolean getRecover() {
        return entityData.get(RECOVER);
    }

    public Integer getGlintColor() {
        return entityData.get(GLINT_COLOR);
    }

    public ResourceLocation getGlintLocation() {
        return ResourceLocation.parse(entityData.get(GLINT_LOCATION));
    }

    public Vector2f getGlintSpeed() {
        return entityData.get(GLINT_SPEED);
    }

    public Vector2f getGlintScale() {
        return entityData.get(GLINT_SCALE);
    }

    public Boolean getGlintAdditive() {
        return entityData.get(GLINT_ADDITIVE);
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
        builder.define(FOIL, false);
        builder.define(RECOVER, false);

        builder.define(GLINT_COLOR, DEFAULT_COLOR);
        builder.define(GLINT_LOCATION, GLINT_DEFAULT.toString());
        builder.define(GLINT_SPEED, new Vector2f(1, 1));
        builder.define(GLINT_SCALE, new Vector2f(1, 1));
        builder.define(GLINT_ADDITIVE, true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        if (HIT_ENTITY && this.getOwner() instanceof Player owner && owner.isAlive() && getRecover()) {
            if (this.distanceToSqr(owner) <= 25 && level().getGameTime() - HIT_TIME >= 10) {
                ItemStack pickupItem = this.getPickupItem();

                if (owner.getInventory().add(pickupItem)) {
                    this.level().playSound(
                            owner,
                            owner.getX(), owner.getY(), owner.getZ(),
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.PLAYERS,
                            1F,
                            (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F
                    );

                    this.discard();

                    return;
                }
            }
        }

        if (COPY_STACK.getItem() instanceof ThrowingSpearItem item)
            item.onTick(this);
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
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (level().isClientSide) return;

        HIT_ENTITY = true;
        HIT_TIME = level().getGameTime();

        float[] damage = {0.0F};

        ItemAttributeModifiers modifiers = COPY_STACK.getOrDefault(
                net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY
        );

        COPY_STACK.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (attributeHolder, modifier) -> {
            if (attributeHolder.is(Objects.requireNonNull(Attributes.ATTACK_DAMAGE.getKey()))) {
                damage[0] += (float) modifier.amount();
            }
        });

        int extraDamage = COPY_STACK.getEnchantmentLevel(this.registryAccess().holderOrThrow(Enchantments.SHARPNESS));
        if (extraDamage > 0)
            damage[0] += 0.5F * extraDamage + 0.5F;

        Entity entity = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().arrow(this, owner);

        if (entity.hurt(source, damage[0]))
            if (entity instanceof LivingEntity livingentity) {
                this.doKnockback(livingentity, source);
                this.doPostHurtEffects(livingentity);
            }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);

        if (COPY_STACK.getItem() instanceof ThrowingSpearItem item)
            item.hitEnemy(this);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (level().getBlockState(result.getBlockPos()).is(Blocks.SLIME_BLOCK)) {
            Vec3 motion = this.getDeltaMovement();

            Direction face = result.getDirection();
            double mx = motion.x;
            double my = motion.y;
            double mz = motion.z;

            switch (face.getAxis()) {
                case X -> mx = -mx * 0.35D;
                case Y -> my = -my * 0.35D;
                case Z -> mz = -mz * 0.35D;
            }

            this.setDeltaMovement(new Vec3(mx, my, mz));

            double horizontalDistance = Math.sqrt(mx * mx + mz * mz);
            this.setYRot((float) (Mth.atan2(mx, mz) * (180D / Math.PI)));
            this.setXRot((float) (Mth.atan2(my, horizontalDistance) * (180D / Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();

            this.playSound(SoundEvents.SLIME_BLOCK_FALL, 1.0F, 1.0F);

            return;
        }

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

            if (COPY_STACK.getItem() instanceof ThrowingSpearItem item)
                item.hitBlock(this);
        }
    }

    @Override
    protected boolean tryPickup(@NotNull Player player) {
        return (this.getOwner() == null || this.ownedBy(player)) && player.getInventory().add(this.getPickupItem());
    }
}
