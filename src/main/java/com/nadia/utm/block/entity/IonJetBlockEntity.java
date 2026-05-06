package com.nadia.utm.block.entity;

import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticleData;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;

import java.util.List;

public class IonJetBlockEntity extends KineticBlockEntity implements BlockEntitySubLevelActor, IHaveGoggleInformation {
    public static float THRUST_MAX = 400;

    public IonJetBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.ION_JET.get(), pos, blockState);
    }

    public float getThrust() {
        float speed = Math.abs(this.getSpeed()) / 256;

        return speed * THRUST_MAX;
    }

    @Override
    public void tick() {
        if (this.level == null || this.getThrust() <= 0) return;

        final BlockPos pos = this.getBlockPos();
        final RandomSource random = this.level.getRandom();
        final BlockState state = this.getBlockState();

        final Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();

        final double speed = getThrust() / 100;
        final float alpha = getThrust() / THRUST_MAX;

        //TODO: maybe a little laggy.
        this.level.addParticle(ParticleTypes.LARGE_SMOKE, true,
                pos.getX() + 0.5 + random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1),
                pos.getY() + 0.5,
                pos.getZ() + 0.5 + random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1),
                facing.getStepX() * speed + ((random.nextDouble() - 0.5) * .2),
                facing.getStepY() * speed + ((random.nextDouble() - 0.5) * .2),
                facing.getStepZ() * speed + ((random.nextDouble() - 0.5) * .2));

        this.level.addParticle(ParticleTypes.SMOKE, true,
                pos.getX() + 0.5 + random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1),
                pos.getY() + 0.5,
                pos.getZ() + 0.5 + random.nextDouble() / 5.0 * (random.nextBoolean() ? 1 : -1),
                facing.getStepX() * speed + ((random.nextDouble() - 0.5) * .2) * alpha,
                facing.getStepY() * speed + ((random.nextDouble() - 0.5) * .2) * alpha,
                facing.getStepZ() * speed + ((random.nextDouble() - 0.5) * .2) * alpha);

        if (random.nextInt(20) == 0) {
            this.level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5,
                    pos.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS,
                    0.25F + random.nextFloat() * .25f, random.nextFloat() * 0.7F + 0.6F, false);
        }

        for (int i = 0; i < Math.floor(getThrust() / THRUST_MAX * 5); i++) {
            this.level.addParticle(new HotAirEmberParticleData(true),
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5f + facing.getStepZ(),
                    pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.5f + facing.getStepZ(),
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5f + facing.getStepZ(),
                    facing.getStepX() * speed + ((random.nextDouble() - 0.5) * .2) * alpha,
                    facing.getStepY() * speed + ((random.nextDouble() - 0.5) * .2) * alpha,
                    facing.getStepZ() * speed + ((random.nextDouble() - 0.5) * .2) * alpha);
        }
    }

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        Vec3 direction = Vec3.atLowerCornerOf(this.getBlockState().getValue(BlockStateProperties.FACING).getNormal());
        Vec3 thrust = direction.scale(getThrust() * timeStep);

        QueuedForceGroup forceGroup = subLevel.getOrCreateQueuedForceGroup(ForceGroups.PROPULSION.get());
        forceGroup.applyAndRecordPointForce(JOMLConversion.atCenterOf(this.getBlockPos()), new Vector3d(thrust.x, thrust.y, thrust.z));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean modified = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        utmLang.text(getSpeed() + " rpm").style(ChatFormatting.WHITE).forGoggles(tooltip);

        return true;
    }
}
