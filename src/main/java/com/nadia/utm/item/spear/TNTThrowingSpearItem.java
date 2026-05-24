package com.nadia.utm.item.spear;

import com.nadia.utm.entity.spear.ThrownSpearEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TNTThrowingSpearItem extends ThrowingSpearItem {
    public TNTThrowingSpearItem(Properties properties, float attackDamage, float attackSpeed) {
        super(properties, attackDamage, attackSpeed);
    }

    public void explode(Vec3 pos, ThrownSpearEntity entity) {
        entity.level().explode(
                entity,
                pos.x, pos.y, pos.z,
                8f, true,
                Level.ExplosionInteraction.BLOCK
        );

        entity.remove(Entity.RemovalReason.KILLED);
    }

    @Override
    public void hitEnemy(ThrownSpearEntity entity) {
        super.hitEnemy(entity);

        explode(entity.position(), entity);
    }

    @Override
    public void hitBlock(ThrownSpearEntity entity) {
        super.hitBlock(entity);

        explode(entity.position(), entity);
    }

    @Override
    public void onTick(ThrownSpearEntity entity) {
        super.onTick(entity);

        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.SMALL_FLAME, entity.position().x, entity.position().y, entity.position().z, 1, 0, 0, 0, 0);
            level.sendParticles(ParticleTypes.SMOKE, entity.position().x, entity.position().y, entity.position().z, 1, 0, 0, 0, 0);
        }
    }
}
