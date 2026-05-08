package com.nadia.utm.block.propulsion;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.*;

import java.util.function.Supplier;

public interface IProduceThrust<T extends BlockEntity & BlockEntitySubLevelActor> {
    @SuppressWarnings("ConstantConditions")
    default void tick(T be, BlockPos worldPosition, float thrust, float thrust_max, Level level, Supplier<ParticleOptions> particleFactory, int range) {
        final BlockPos pos = be.getBlockPos();
        final RandomSource random = level.getRandom();
        final BlockState state = be.getBlockState();

        final Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();

        final double speed = (thrust / 100) * 1.5;
        final float alpha = thrust / thrust_max;
        final SubLevel sublevel = (SubLevel) SableCompanion.INSTANCE.getContaining(be);
        final Vec3i dir = facing.getNormal().multiply((int) (range * alpha));
        Vec3 start = worldPosition.getCenter().add(new Vec3(facing.getNormal().getX(), facing.getNormal().getY(), facing.getNormal().getZ()).multiply(.5, .5, .5));
        Vec3 end = worldPosition.getCenter().add(new Vec3(dir.getX(), dir.getY(), dir.getZ()));
        if (sublevel != null) {
            start = sublevel.logicalPose().transformPosition(start);
            end = sublevel.logicalPose().transformPosition(end);
        }

        if (level.isClientSide()) {
            for (int i = 0; i < Math.floor(thrust / thrust_max * 3); i++)
                level.addParticle(particleFactory.get(),
                        pos.getX() + 0.5 + (random.nextDouble() - 0.5) + facing.getStepZ(),
                        pos.getY() + 0.5 + (random.nextDouble() - 0.5) + facing.getStepZ(),
                        pos.getZ() + 0.5 + (random.nextDouble() - 0.5) + facing.getStepZ(),
                        facing.getStepX() * speed + ((random.nextDouble() - 0.5) * .3) * alpha,
                        facing.getStepY() * speed + ((random.nextDouble() - 0.5) * .3) * alpha,
                        facing.getStepZ() * speed + ((random.nextDouble() - 0.5) * .3) * alpha);

            BlockHitResult clip = level.clip(new ClipContext(
                    start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, (Entity) null
            ));

            if (clip.getType() != HitResult.Type.MISS && level.getGameTime() % 5 == 0) {
                final Vec3 worldPos = clip.getLocation();
                final Direction face = clip.getDirection();
                final Vec3 normal = Vec3.atLowerCornerOf(face.getNormal());
                final Vec3 targetPos = worldPos.add(normal.scale(0.05));

                Vec3 tDir = new Vec3(
                        random.nextDouble() - 0.5,
                        random.nextDouble() - 0.5,
                        random.nextDouble() - 0.5
                );

                Vec3 slideVel = tDir.subtract(normal.scale(tDir.dot(normal)))
                        .normalize()
                        .scale(0.1 + random.nextDouble() * 0.1);

                level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        targetPos.x, targetPos.y, targetPos.z,
                        slideVel.x, slideVel.y, slideVel.z
                );
            }
        } else {
            final AABB search = new AABB(start, end).inflate(1);
            EntityHitResult result = ProjectileUtil.getEntityHitResult(
                    level,
                    null,
                    start,
                    end,
                    search,
                    entity -> (entity instanceof LivingEntity || entity instanceof ItemEntity) && entity.isAlive() && !entity.isSpectator()
            );


            if (result != null) {
                Vec3 forceDir = Vec3.atLowerCornerOf(be.getBlockState().getValue(BlockStateProperties.FACING).getOpposite().getNormal());
                if (sublevel != null) forceDir = sublevel.logicalPose().transformNormal(forceDir);
                result.getEntity().addDeltaMovement(new Vec3(forceDir.x * speed / 50, forceDir.y * speed / 50, forceDir.z * speed / 50));

                if (result.getEntity() instanceof LivingEntity entity) {
                    entity.hurt(level.damageSources().inFire(), 0.25f);
                    entity.invulnerableTime = 0;
                }
            }
        }
    }
}
