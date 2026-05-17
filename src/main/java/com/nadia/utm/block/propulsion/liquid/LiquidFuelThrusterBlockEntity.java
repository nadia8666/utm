package com.nadia.utm.block.propulsion.liquid;

import com.nadia.utm.block.propulsion.IProduceThrust;
import com.nadia.utm.config.utmServerConfig;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Vector3d;

import java.util.List;

@ForceLoad
public class LiquidFuelThrusterBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelActor, IHaveGoggleInformation, IProduceThrust<LiquidFuelThrusterBlockEntity> {
    public SmartFluidTankBehaviour FUEL;
    public int REDSTONE = 0;
    public final LerpedFloat THRUST_FORCE = LerpedFloat.linear();

    public LiquidFuelThrusterBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.LIQUID_THRUSTER.get(), pos, blockState);

        THRUST_FORCE.chase(0, 8, LerpedFloat.Chaser.LINEAR);
    }

    public static float getThrustMax() {
        return utmServerConfig.LIQUID_THRUSTER_FORCE.get();
    }

    public float getThrottle() {
        return REDSTONE / 15F;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.FUEL = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 8000, true);
        this.FUEL.getPrimaryHandler().setValidator((fluid) -> fluid.is(Tags.Fluids.LAVA));

        behaviours.add(this.FUEL);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        THRUST_FORCE.setValueNoUpdate(compound.getFloat("Thrust"));
        REDSTONE = compound.getInt("Redstone");
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("Thrust", THRUST_FORCE.getValue());
        compound.putInt("Redstone", REDSTONE);
    }

    public void updateThrust() {
        THRUST_FORCE.updateChaseTarget(getThrustRaw());
    }

    public float getThrust() {
        return THRUST_FORCE.getValue();
    }

    public float getThrustRaw() {
        if (this.FUEL.getPrimaryHandler().getFluidAmount() <= 250) return 0;

        return getThrottle() * getThrustMax();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level == null || level.isClientSide()) return;
        int targetThrottle = this.level.getBestNeighborSignal(worldPosition);

        if (REDSTONE != targetThrottle) {
            REDSTONE = targetThrottle;
            sendData();
        }

        updateThrust();
        THRUST_FORCE.tickChaser();

        if (this.getThrust() <= 0) return;
        this.FUEL.getPrimaryHandler().drain((int) ((THRUST_FORCE.getValue() / getThrustMax()) * 5), IFluidHandler.FluidAction.EXECUTE);

        tick(this, worldPosition, getThrust(), getThrustMax(), level, () -> new HotAirEmberParticleData(false), 20);
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
        utmLang.text((int) (getThrottle() * 100) + "%").style(ChatFormatting.AQUA).space().add(utmLang.text("throttle").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text((int) getThrust() + "N").style(ChatFormatting.AQUA).space().add(utmLang.text("per tick").style(ChatFormatting.GRAY)).forGoggles(tooltip);

        return true;
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, (event) -> event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                utmBlockEntities.LIQUID_THRUSTER.get(),
                (be, side) -> be.FUEL.getCapability()
        ));
    }
}
