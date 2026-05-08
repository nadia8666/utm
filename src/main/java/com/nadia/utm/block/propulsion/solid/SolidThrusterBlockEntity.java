package com.nadia.utm.block.propulsion.solid;

import com.nadia.utm.Config;
import com.nadia.utm.block.propulsion.IProduceThrust;
import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.tags.utmTags;
import com.nadia.utm.util.PosUtil;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticleData;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolidThrusterBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelActor, BlockEntitySubLevelActorExtensions<SolidThrusterBlockEntity>, IHaveGoggleInformation, IProduceThrust<SolidThrusterBlockEntity> {
    public Set<BlockPos> FUEL = new HashSet<>();
    public Set<BlockPos> READ = new HashSet<>();
    public boolean ACTIVATED = false;
    public int TICKS_ELAPSED = 0;
    public LerpedFloat THRUST_FORCE = LerpedFloat.linear();

    public SolidThrusterBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.SOLID_THRUSTER.get(), pos, blockState);

        THRUST_FORCE.chase(0, 65, LerpedFloat.Chaser.LINEAR);
    }

    public static float getThrustMax() {
        return Config.SOLID_THRUSTER_FORCE.get();
    }

    public void updateThrust() {
        THRUST_FORCE.updateChaseTarget(getThrustRaw());
    }

    public float getThrust() {
        return THRUST_FORCE.getValue();
    }

    public float getThrustRaw() {
        if (ACTIVATED) {
            if (FUEL.isEmpty())
                return 0;

            return getThrustMax();
        }

        return 0;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        THRUST_FORCE.setValueNoUpdate(compound.getFloat("Thrust"));
        ACTIVATED = compound.getBoolean("Activated");

        FUEL.clear();
        for (long pos : compound.getLongArray("FuelList"))
            FUEL.add(BlockPos.of(pos));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("Thrust", THRUST_FORCE.getValue());
        compound.putBoolean("Activated", ACTIVATED);
        compound.putLongArray("FuelList", FUEL.stream().mapToLong(BlockPos::asLong).toArray());
    }


    public void updateFuel() {
        READ.clear();
        FUEL.clear();

        deepScan(worldPosition);
    }

    public void deepScan(BlockPos pos) {
        if (level == null || READ.contains(pos)) return;

        for (BlockPos p : PosUtil.getAdjacent(pos)) {
            BlockState state = level.getBlockState(p);
            if (state.is(utmTags.BLOCK.SOLID_ROCKET_FUEL) && !FUEL.contains(p)) {
                FUEL.add(p);
                deepScan(p);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level != null && !ACTIVATED && !level.isClientSide()) {
            ACTIVATED = level.hasNeighborSignal(worldPosition);

            if (ACTIVATED) updateFuel();
        }

        if (level != null && !level.isClientSide()) {
            updateThrust();
            THRUST_FORCE.tickChaser();

            sendData();
        }

        if (this.level == null || this.getThrust() <= 0) return;

        final RandomSource random = this.level.getRandom();
        final BlockState state = this.getBlockState();

        final Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();

        FUEL.stream()
                .max(Comparator.comparingDouble(p -> p.distSqr(worldPosition)))
                .ifPresent(block -> {
                    if (!level.isClientSide()) {
                        boolean isInvalid = !level.getBlockState(block).is(utmTags.BLOCK.SOLID_ROCKET_FUEL);

                        TICKS_ELAPSED = (TICKS_ELAPSED + 1) % 100;
                        if (TICKS_ELAPSED == 0 || isInvalid)
                            if (level.setBlock(block, Blocks.AIR.defaultBlockState(), 3) || isInvalid)
                                FUEL.remove(block);
                    } else if (level.getGameTime() % 2 == 0)
                        level.addParticle(ParticleTypes.FLAME,
                                block.getCenter().x + random.nextDouble() * 1.1 - 0.5,
                                block.getCenter().y + random.nextDouble() * 1.1 - 0.5,
                                block.getCenter().z + random.nextDouble() * 1.1 - 0.5,
                                0, 0, 0);
                });

        tick(this, worldPosition, getThrust(), getThrustMax(), level, () -> new HotAirEmberParticleData(false), 30);
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
        utmLang.text("Thrust Info:").forGoggles(tooltip, 0);
        utmLang.text("Engine").space().add(utmLang.text(ACTIVATED ? "activated" : "disabled").style(ChatFormatting.AQUA)).forGoggles(tooltip);
        utmLang.text((int) getThrust() + "N").style(ChatFormatting.AQUA).space().add(utmLang.text("per tick").style(ChatFormatting.GRAY)).forGoggles(tooltip);

        return true;
    }
}
