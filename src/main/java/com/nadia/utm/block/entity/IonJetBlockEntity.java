package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
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
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Vector3d;

import java.util.List;

@ForceLoad
public class IonJetBlockEntity extends KineticBlockEntity implements BlockEntitySubLevelActor, IHaveGoggleInformation {
    public SmartFluidTankBehaviour LOX;
    public static float THRUST_MAX = 400;
    public LerpedFloat THRUST_ALPHA = LerpedFloat.linear();

    public IonJetBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.ION_JET.get(), pos, blockState);

        THRUST_ALPHA.chase(0, 10, LerpedFloat.Chaser.LINEAR);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.LOX = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true);
        this.LOX.getPrimaryHandler().setValidator((fluid) -> fluid.is(utmFluids.LIQUID_OXYGEN));

        behaviours.add(this.LOX);
    }

    public void updateThrust() {
        THRUST_ALPHA.updateChaseTarget(getThrustRaw());
    }

    public float getThrust() {
        return THRUST_ALPHA.getValue();
    }

    public float getThrustRaw() {
        if (this.LOX.getPrimaryHandler().getFluidAmount() <= 250) return 0;

        float speed = Math.abs(this.getSpeed()) / 256;

        return speed * THRUST_MAX;
    }

    @Override
    public void tick() {
        super.tick();

        updateThrust();
        THRUST_ALPHA.tickChaser();

        if (this.level == null || this.getThrust() <= 0) return;

        this.LOX.getPrimaryHandler().drain(5, IFluidHandler.FluidAction.EXECUTE);

        final BlockPos pos = this.getBlockPos();
        final RandomSource random = this.level.getRandom();
        final BlockState state = this.getBlockState();

        final Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();

        final double speed = getThrust() / 100;
        final float alpha = getThrust() / THRUST_MAX;

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
        containedFluidTooltip(tooltip, isPlayerSneaking, this.LOX.getCapability());
        tooltip.add(Component.empty());
        boolean modified = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        tooltip.add(Component.empty());
        utmLang.text(getSpeed() + " rpm").style(ChatFormatting.WHITE).forGoggles(tooltip);
        utmLang.text(getThrust() + " thrust").style(ChatFormatting.AQUA).forGoggles(tooltip);

        return true;
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, (event) -> {
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    utmBlockEntities.ION_JET.get(),
                    (be, side) -> be.LOX.getCapability()
            );
        });
    }
}
