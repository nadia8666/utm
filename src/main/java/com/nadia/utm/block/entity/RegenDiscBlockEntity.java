package com.nadia.utm.block.entity;

import com.nadia.utm.block.RegenDiscBlock;
import com.nadia.utm.client.renderer.IBypassRPM;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.List;

public class RegenDiscBlockEntity extends GeneratingKineticBlockEntity implements BlockEntitySubLevelActor, IBypassRPM {
    public float SPEED = 0;

    public RegenDiscBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.REGEN_DISC.get(), pos, state);
    }

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        Vector3d localSpeed = subLevel.logicalPose().transformNormalInverse(handle.getAngularVelocity(new Vector3d()));
        SPEED = (float) (Math.abs(localSpeed.y) * (Math.min(30, Math.abs(localSpeed.y))) / Math.PI);

        setChanged();
        sendData();

        updateGeneratedRotation();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean modified = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        useTooltip(tooltip, this);

        return true;
    }

    @Override
    public boolean isSpeedRequirementFulfilled() {
        return SPEED >= 1024;
    }

    @Override
    public float getGeneratedSpeed() {
        return convertToDirection(SPEED < 1024 ? 0 : SPEED, getBlockState().getValue(RegenDiscBlock.FACING));
    }

    @Override
    protected void read(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        SPEED = tag.getFloat("GenerativeSpeed");
    }

    @Override
    public void write(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("GenerativeSpeed", SPEED);
    }
}
