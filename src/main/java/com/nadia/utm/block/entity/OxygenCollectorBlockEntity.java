package com.nadia.utm.block.entity;

import com.nadia.utm.block.entity.components.OutputOnlyTank;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.util.utmLang;
import com.nadia.utm.utm;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.particle.AirParticleData;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

@ForceLoad
public class OxygenCollectorBlockEntity extends SplitShaftBlockEntity implements IHaveGoggleInformation {
    public static final int MAX_AIR = 1000;
    public double FILL_SPEED = 0;
    public int COLLECTION_RATE = 0;

    public final OutputOnlyTank TANK = new OutputOnlyTank(MAX_AIR);

    public OxygenCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.OXYGEN_COLLECTOR.get(), pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction direction) {
        return 1;
    }

    @Override
    public void tick() {
        super.tick();

        if (level != null) {
            double str = OxyUtil.getCollectionStrength(level, this.getBlockPos());
            FILL_SPEED = str;

            float speed = Math.abs(getSpeed());
            int amount = (int) (speed / 40);

            if (amount > 0) {
                int rate = Math.clamp(Mth.floor(amount * str), 0, Math.max(MAX_AIR - TANK.getFluidAmount(), 0));
                COLLECTION_RATE = rate;

                if (!level.isClientSide() && rate > 0) {
                    TANK.forceFill(new FluidStack(utmFluids.LIQUID_OXYGEN, rate), IFluidHandler.FluidAction.EXECUTE);

                    if (level.getGameTime() % 20 == 0 || TANK.getFluidAmount() == MAX_AIR) {
                        sendData();

                        List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, new AABB(worldPosition).inflate(8.0));
                        for (ServerPlayer player : players)
                            AdvancementUtil.AwardAdvancement(player, utm.key("2313ag/oxygen_collector"));
                    }
                } else if (rate > 0) {
                    Vec3 center = VecHelper.getCenterOf(this.worldPosition);
                    Vec3 pos = VecHelper.offsetRandomly(center.add(0, 1, 0), this.level.random, 0.5F);
                    Vec3 spd = center.subtract(pos).multiply(speed / 128, speed / 128, speed / 128);
                    this.level.addParticle(new AirParticleData(1.0F, 0.05F), pos.x, pos.y, pos.z, spd.x, spd.y, spd.z);
                }
            }
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        TANK.readFromNBT(registries, compound.getCompound("Fluid"));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Fluid", TANK.writeToNBT(registries, new CompoundTag()));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean modified = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        tooltip.add(Component.empty());
        modified = super.containedFluidTooltip(tooltip, isPlayerSneaking, this.TANK) || modified;

        Level level = this.getLevel();
        if (level == null) return modified;

        int counted = (int) OxyUtil.getCollectionStrength(level, this.getBlockPos(), null, true);
        String efficiency = String.format("%.1f", FILL_SPEED * 100);

        tooltip.add(Component.empty());
        utmLang.text("Collection Info:").style(ChatFormatting.WHITE).forGoggles(tooltip);
        utmLang.text(TANK.getFluidAmount() + "/" + MAX_AIR).style(ChatFormatting.AQUA).space().add(utmLang.text("Oxygen").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text(String.valueOf(COLLECTION_RATE * 20)).style(ChatFormatting.AQUA).space().add(utmLang.text("Oxygen per second").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text(efficiency + "% Efficiency").style(
                FILL_SPEED > .85 ? ChatFormatting.GOLD :
                        FILL_SPEED > .5 ? ChatFormatting.GREEN :
                        FILL_SPEED > .2 ? ChatFormatting.RED :
                        ChatFormatting.DARK_RED
        ).forGoggles(tooltip);
        utmLang.text(counted + "/342").style(ChatFormatting.AQUA).space().add(utmLang.text("Leaves").style(ChatFormatting.GRAY)).forGoggles(tooltip);

        return true;
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, event -> event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                utmBlockEntities.OXYGEN_COLLECTOR.get(),
                (be, side) -> (side != Direction.DOWN && side != Direction.UP) ? be.TANK : null
        ));
    }
}
