package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ForceLoad
public abstract class AbstractSealerBlockEntity extends SplitShaftBlockEntity implements IHaveGoggleInformation {
    public SmartFluidTankBehaviour TANK;
    public CombinedTankWrapper CAPABILITY;

    public Set<BlockPos> ATTACHED_POSITIONS = new HashSet<>();
    protected final Queue<BlockPos> QUEUE = new LinkedList<>();
    protected final Set<BlockPos> VISITED = new HashSet<>();

    public int SYNCED_VOLUME = 0;
    public boolean ACTIVE = false;
    protected boolean RECALC = false; // stands for calculating

    public AbstractSealerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction direction) {
        return 1;
    }

    public int getMaxVolume() {
        return 67;
    }

    public int getDraw() {
        return 2;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        boolean hasOxygen = TANK.getPrimaryHandler().getFluidAmount() > 0;

        if (hasOxygen != ACTIVE) {
            ACTIVE = hasOxygen;
            if (ACTIVE) seal();
            else unseal();
            this.sendData();
        }

        if (ACTIVE) {
            if (RECALC)
                process();

            TANK.getPrimaryHandler().drain(getDraw(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public void seal() {
        if (!(level instanceof ServerLevel)) return;
        this.RECALC = true;
        this.QUEUE.clear();
        this.VISITED.clear();
        this.QUEUE.addAll(getAdjacent(worldPosition));
    }

    protected void process() {
        ServerLevel sLevel = (ServerLevel) level;
        if (sLevel == null) return;

        int processed = 0;

        while (!QUEUE.isEmpty() && processed < 500) {
            BlockPos current = QUEUE.poll();
            processed++;

            if (VISITED.size() >= getMaxVolume()) break;
            if (VISITED.contains(current)) continue;

            BlockState state = sLevel.getBlockState(current);
            if (state.isAir() || !state.getCollisionShape(sLevel, current).equals(Shapes.block())) {
                VISITED.add(current);

                if (!worldPosition.equals(OxyUtil.isSealed(sLevel, current)))
                    OxyUtil.setBlockSealed(sLevel, current, worldPosition);

                for (BlockPos neighbor : getAdjacent(current))
                    if (!VISITED.contains(neighbor)) QUEUE.add(neighbor);
            }
        }

        if (QUEUE.isEmpty() || VISITED.size() >= getMaxVolume()) {
            finalizeR();
        }
    }

    // i wanted to make this finalize but.. it overrides.. java 9 object arg??? wtf./
    protected void finalizeR() {
        ServerLevel sLevel = (ServerLevel) level;
        if (sLevel == null) return;

        for (BlockPos oldPos : ATTACHED_POSITIONS)
            if (!VISITED.contains(oldPos))
                OxyUtil.setBlockSealed(sLevel, oldPos, null);

        ATTACHED_POSITIONS.clear();
        ATTACHED_POSITIONS.addAll(VISITED);
        SYNCED_VOLUME = ATTACHED_POSITIONS.size();
        RECALC = false;

        this.setChanged();
        this.sendData();
    }

    public void unseal() {
        if (level instanceof ServerLevel slevel)
            for (BlockPos pos : ATTACHED_POSITIONS)
                OxyUtil.setBlockSealed(slevel, pos, null);
        ATTACHED_POSITIONS.clear();
        SYNCED_VOLUME = 0;
        RECALC = false;
        QUEUE.clear();
        this.setChanged();
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
        if (!clientPacket) {
            ATTACHED_POSITIONS.clear();
            for (long p : tag.getLongArray("Positions")) ATTACHED_POSITIONS.add(BlockPos.of(p));
        }
    }

    @Override
    public void write(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putBoolean("Active", ACTIVE);
        tag.putInt("SyncedVolume", SYNCED_VOLUME);
        if (!clientPacket)
            tag.putLongArray("Positions", ATTACHED_POSITIONS.stream().map(BlockPos::asLong).toList());
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

    protected static List<BlockPos> getAdjacent(BlockPos pos) {
        return List.of(pos.above(), pos.below(), pos.north(), pos.east(), pos.south(), pos.west());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip, isPlayerSneaking, this.TANK.getCapability());
        tooltip.add(Component.empty());
        utmLang.text("Sealing Info:").style(ChatFormatting.WHITE).forGoggles(tooltip);
        utmLang.text("SEALING " + (ACTIVE ? "ACTIVE" : "INACTIVE")).style(ACTIVE ? ChatFormatting.GREEN : ChatFormatting.DARK_RED).forGoggles(tooltip);
        utmLang.text(SYNCED_VOLUME + "/" + getMaxVolume()).style(ChatFormatting.AQUA).space().add(utmLang.text("Sealed Blocks").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text(String.valueOf(getDraw() * 20)).style(ChatFormatting.AQUA).space().add(utmLang.text("Oxygen used per second").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        return true;
    }

    static {
        utmEvents.register(BlockEvent.EntityPlaceEvent.class, event -> handleWorldChange(event, event.getPos()));
        utmEvents.register(BlockEvent.BreakEvent.class, event -> handleWorldChange(event, event.getPos()));
        utmEvents.register(BlockEvent.FluidPlaceBlockEvent.class, event -> handleWorldChange(event, event.getPos()));
    }

    private static void handleWorldChange(BlockEvent event, BlockPos pos) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos controllerPos = OxyUtil.isSealed(level, pos);

        if (controllerPos == null)
            for (BlockPos neighbor : List.of(pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west())) {
                controllerPos = OxyUtil.isSealed(level, neighbor);
                if (controllerPos != null) break;
            }

        if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof AbstractSealerBlockEntity be)
            if (be.ACTIVE) be.seal();
    }
}