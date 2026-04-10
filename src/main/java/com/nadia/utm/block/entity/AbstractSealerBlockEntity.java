package com.nadia.utm.block.entity;

import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractSealerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public SmartFluidTankBehaviour TANK;
    public CombinedTankWrapper CAPABILITY;
    public Set<BlockPos> ATTACHED_POSITIONS = new HashSet<>();
    public static int MAX_VOLUME = 67;
    public int SYNCED_VOLUME = 0;
    public boolean ACTIVE = false;

    public AbstractSealerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.TANK = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true);
        this.TANK.getPrimaryHandler().setValidator((fluid) -> fluid.is(utmFluids.LIQUID_OXYGEN));

        CAPABILITY = new CombinedTankWrapper(this.TANK.getCapability());

        behaviours.add(this.TANK);
    }

    @Override
    protected void read(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        ACTIVE = tag.getBoolean("Active");
        SYNCED_VOLUME = tag.getInt("SyncedVolume");

        ATTACHED_POSITIONS.clear();
        for (long posLong : tag.getLongArray("AttachedPositions"))
            ATTACHED_POSITIONS.add(BlockPos.of(posLong));
    }

    @Override
    public void write(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        tag.putBoolean("Active", ACTIVE);
        tag.putInt("SyncedVolume", SYNCED_VOLUME);

        long[] positionsArray = ATTACHED_POSITIONS.stream()
                .mapToLong(BlockPos::asLong)
                .toArray();
        tag.putLongArray("AttachedPositions", positionsArray);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);

        SYNCED_VOLUME = tag.getInt("SyncedVolume");
        ACTIVE = tag.getBoolean("Active");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider lookupProvider) {
        CompoundTag tag = super.getUpdateTag(lookupProvider);

        tag.putInt("SyncedVolume", SYNCED_VOLUME);
        tag.putBoolean("Active", ACTIVE);
        return tag;
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) return;
        ;

        boolean shouldExecute = TANK.getPrimaryHandler().getFluidAmount() > 0;
        if (shouldExecute != ACTIVE) {
            ACTIVE = shouldExecute;
            this.setChanged();

            if (ACTIVE)
                seal();
            else
                unseal();
        }

        if (ACTIVE) {
            TANK.getPrimaryHandler().drain(2, IFluidHandler.FluidAction.EXECUTE);
            this.setChanged();
        }
    }

    public void unseal() {
        if (ATTACHED_POSITIONS.isEmpty()) return;

        if (level instanceof ServerLevel slevel)
            for (BlockPos pos : ATTACHED_POSITIONS)
                OxyUtil.setBlockSealed(slevel, pos, null);

        ATTACHED_POSITIONS.clear();
        SYNCED_VOLUME = 0;
    }

    public void seal() {
        if (!(level instanceof ServerLevel slevel)) return;

        if (!ATTACHED_POSITIONS.isEmpty())
            unseal();

        Queue<BlockPos> queue = new LinkedList<>(getAdjacent(getBlockPos()));

        while (!queue.isEmpty() && ATTACHED_POSITIONS.size() < MAX_VOLUME) {
            BlockPos target = queue.poll();

            if (!ATTACHED_POSITIONS.contains(target) && slevel.getBlockState(target).isAir()) {
                OxyUtil.setBlockSealed(slevel, target, getBlockPos());
                ATTACHED_POSITIONS.add(target);
                queue.addAll(getAdjacent(target));
            }
        }

        SYNCED_VOLUME = ATTACHED_POSITIONS.size();
    }

    protected static List<BlockPos> getAdjacent(BlockPos pos) {
        return List.of(pos.above(), pos.below(), pos.north(), pos.east(), pos.south(), pos.west());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip, isPlayerSneaking, this.TANK.getCapability());

        tooltip.add(Component.empty());
        utmLang.text("Sealing Info:").style(ChatFormatting.WHITE).forGoggles(tooltip);
        utmLang.text("SEALING " + (ACTIVE ? "ACTIVE" : "INACTIVE")).style(ACTIVE ? ChatFormatting.GREEN : ChatFormatting.DARK_RED).forGoggles(tooltip);
        utmLang.text(SYNCED_VOLUME + "/" + MAX_VOLUME).style(ChatFormatting.AQUA).space().add(utmLang.text("Sealed Blocks").style(ChatFormatting.GRAY)).forGoggles(tooltip);

        return true;
    }
}
