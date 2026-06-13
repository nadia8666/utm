package com.nadia.utm.entity.projectile;

import com.nadia.utm.registry.entity.utmEntities;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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


public class AridStoneEntity extends ThrowableItemProjectile {
    //todo: when you make another projectile make a standardprojectilesrenderer and forceload that instead

    public AridStoneEntity(Level level) {
        super(utmEntities.ARID_STONE.get(), level);
    }


    //my code? deobfustcated
    public AridStoneEntity(EntityType<? extends AridStoneEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AridStoneEntity(Level level, LivingEntity shooter) {
        super(utmEntities.ARID_STONE.get(), shooter, level); //entitytype
    }

    public AridStoneEntity(Level level, double x, double y, double z) {
        super(utmEntities.ARID_STONE.get(), x, y, z, level);
    }

    protected @NotNull Item getDefaultItem() {
        return utmItems.ARID_INGOT.get();
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
        int i = 3;
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float)i);
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

    }

}
