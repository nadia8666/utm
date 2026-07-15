package com.nadia.utm.entity.projectile;

import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.registry.sound.utmSounds;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;


public class PebbleEntity extends ThrowableItemProjectile {
    //todo: when you make another projectile make a standardprojectilesrenderer and forceload that instead

    public PebbleEntity(Level level) {
        super(utmEntities.PEBBLE.get(), level);
    }


    //my code? deobfustcated
    public PebbleEntity(EntityType<? extends PebbleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PebbleEntity(Level level, LivingEntity shooter) {
        super(utmEntities.PEBBLE.get(), shooter, level); //entitytype
    }

    public PebbleEntity(Level level, double x, double y, double z) {
        super(utmEntities.PEBBLE.get(), x, y, z, level);
    }

    protected @NotNull Item getDefaultItem() {
        return utmItems.PEBBLE_BULLET.get();
    }

    private ParticleOptions getParticle() {
        return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this::getDefaultItem));
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        float i = 0.75f;
        entity.level().playSound(entity,
                entity.getBlockPosBelowThatAffectsMyMovement(),
                utmSounds.SR_HIT.get(),
                SoundSource.NEUTRAL,0.1f,1);
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), i);
        if (this.getRemainingFireTicks()>0)
             entity.setRemainingFireTicks(this.getRemainingFireTicks());
        entity.invulnerableTime=0;
    }

    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

    }

}
