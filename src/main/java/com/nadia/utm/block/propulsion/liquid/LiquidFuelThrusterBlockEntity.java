package com.nadia.utm.block.propulsion.liquid;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dev.eriksonn.aeronautics.content.particle.HotAirEmberParticleData;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.List;

@ForceLoad
public class LiquidFuelThrusterBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelActor, IHaveGoggleInformation {
    public SmartFluidTankBehaviour FUEL;
    public static float THRUST_MAX = 850; // TODO: make configurable on ionjet, liquid fuel, and solid fuel
    public float REDSTONE = 0;
    public LerpedFloat THRUST_FORCE = LerpedFloat.linear();

    public LiquidFuelThrusterBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.LIQUID_THRUSTER.get(), pos, blockState);

        THRUST_FORCE.chase(0, 8, LerpedFloat.Chaser.LINEAR);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.FUEL = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 5000, true);
        this.FUEL.getPrimaryHandler().setValidator((fluid) -> fluid.is(Tags.Fluids.LAVA));

        behaviours.add(this.FUEL);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        THRUST_FORCE.setValueNoUpdate(compound.getFloat("Thrust"));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("Thrust", THRUST_FORCE.getValue());
    }

    public void updateThrust() {
        THRUST_FORCE.updateChaseTarget(getThrustRaw());
    }

    public float getThrust() {
        return THRUST_FORCE.getValue();
    }

    public float getThrustRaw() {
        if (this.FUEL.getPrimaryHandler().getFluidAmount() <= 250) return 0;

        return REDSTONE * THRUST_MAX;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level != null) {
            REDSTONE = this.level.getBestNeighborSignal(worldPosition) / 15F;
        }

        updateThrust();
        THRUST_FORCE.tickChaser();

        if (this.level == null || this.getThrust() <= 0) return;

        this.FUEL.getPrimaryHandler().drain((int) ((THRUST_FORCE.getValue() / THRUST_MAX) * 50), IFluidHandler.FluidAction.EXECUTE);

        final BlockPos pos = this.getBlockPos();
        final RandomSource random = this.level.getRandom();
        final BlockState state = this.getBlockState();

        final Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();

        final double speed = (getThrust() / 100) * 1.5;
        final float alpha = getThrust() / THRUST_MAX;

        if (level.isClientSide())
            for (int i = 0; i < Math.floor(getThrust() / THRUST_MAX * 5); i++)
                this.level.addParticle(new HotAirEmberParticleData(false),
                        pos.getX() + 0.5 + (random.nextDouble() - 0.5) + facing.getStepZ(),
                        pos.getY() + 0.5 + (random.nextDouble() - 0.5) + facing.getStepZ(),
                        pos.getZ() + 0.5 + (random.nextDouble() - 0.5) + facing.getStepZ(),
                        facing.getStepX() * speed + ((random.nextDouble() - 0.5) * .3) * alpha,
                        facing.getStepY() * speed + ((random.nextDouble() - 0.5) * .3) * alpha,
                        facing.getStepZ() * speed + ((random.nextDouble() - 0.5) * .3) * alpha);
        else {
            final Quaternionf quat = facing.getRotation();

            final double dist = 20 * alpha;
            double radius = 0.5;
            double offsetMin = 0;

            final Vector3d max = new Vector3d(radius, dist, radius);
            final Vector3d min = new Vector3d(-radius, offsetMin, -radius);

            quat.transform(max);
            quat.transform(min);

            min.add(JOMLConversion.toJOML(this.worldPosition.getCenter()));
            max.add(JOMLConversion.toJOML(this.worldPosition.getCenter()));

            final BoundingBox3d aabb = new BoundingBox3d(min.x, min.y, min.z, max.x, max.y, max.z);

            Vec3 a = Vec3.atLowerCornerOf(this.getBlockState().getValue(BlockStateProperties.FACING).getOpposite().getNormal());
            Vector3d forceDir = new Vector3d(a.x, a.y, a.z);
            final SubLevel subLevel = Sable.HELPER.getContaining(this);
            if (subLevel != null) {
                aabb.transform(subLevel.logicalPose(), aabb);

                subLevel.logicalPose().transformNormal(forceDir);
            }

            final List<Entity> entities = level.getEntities(null, aabb.toMojang());
            for (Entity entity : entities)
                if (entity instanceof LivingEntity) {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(
                            forceDir.x * speed / 100,
                            forceDir.y * speed / 100,
                            forceDir.z * speed / 100
                    ));

                    entity.hurt(level.damageSources().inFire(), 1);
                    entity.invulnerableTime = 5;
                }
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
        containedFluidTooltip(tooltip, isPlayerSneaking, this.FUEL.getCapability());
        tooltip.add(Component.empty());

        utmLang.text("Thrust Info:").forGoggles(tooltip, 0);
        utmLang.text((int) (REDSTONE * 100) + "%").style(ChatFormatting.AQUA).space().add(utmLang.text("throttle").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text((int) getThrust() + "N").style(ChatFormatting.AQUA).space().add(utmLang.text("per tick").style(ChatFormatting.GRAY)).forGoggles(tooltip);

        return true;
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, (event) -> {
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    utmBlockEntities.LIQUID_THRUSTER.get(),
                    (be, side) -> be.FUEL.getCapability()
            );
        });
    }
}
