package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.events.BlockStateChangedEvent;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.util.SableUtil;
import com.nadia.utm.util.utmLang;
import com.nadia.utm.utm;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
    protected final Set<BlockPos> SEALED = new HashSet<>();

    public int SYNCED_VOLUME = 0;
    public boolean ACTIVE = false;
    protected boolean RECALC = false; // stands for calculating

    public enum SEAL_TYPE {
        UNSEALED,
        SEALED,
        SEAL_NO_PROP // sealed w/o propagation
    }

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
        step();
    }

    public void step() {
        if (level == null || level.isClientSide) return;
        boolean hasOxygen = TANK.getPrimaryHandler().getFluidAmount() > 0;

        if (hasOxygen != ACTIVE) {
            ACTIVE = hasOxygen;
            if (ACTIVE) seal();
            else unseal();
            sendData();
        }

        if (ACTIVE) {
            if (RECALC)
                process();

            TANK.getPrimaryHandler().drain(getDraw(), IFluidHandler.FluidAction.EXECUTE);

            List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, new AABB(worldPosition).inflate(8.0));
            for (ServerPlayer player : players)
                AdvancementUtil.AwardAdvancement(player, utm.key("2313ag/sealed"));
        }
    }

    public void seal() {
        if (!(level instanceof ServerLevel)) return;
        RECALC = true;
        QUEUE.clear();
        VISITED.clear();
        SEALED.clear();
        QUEUE.addAll(getAdjacent(worldPosition));
    }

    protected void process() {
        ServerLevel sLevel = (ServerLevel) level;
        if (sLevel == null) return;

        int processed = 0;
        BlockPos lastPos = worldPosition;

        SubLevel level = null;
        if (SableCompanion.INSTANCE.getContaining(this) instanceof SubLevel slevel)
            level = slevel;

        while (!QUEUE.isEmpty() && processed < 500) {
            BlockPos current = QUEUE.poll();
            processed++;

            if (SEALED.size() >= getMaxVolume()) break;
            if (VISITED.contains(current)) continue;
            VISITED.add(current);

            BlockState state = level != null ? SableUtil.getState(level, current) : sLevel.getBlockState(current);
            SEAL_TYPE sealable = state == null ? SEAL_TYPE.UNSEALED : canSeal(state, sLevel, current, lastPos);
            lastPos = current;
            if (sealable != SEAL_TYPE.UNSEALED) {
                SEALED.add(current);
                if (level != null)
                    OxyUtil.setBlockSealed(level, current, worldPosition);
                else
                    OxyUtil.setBlockSealed(sLevel, current, worldPosition);

                if (sealable == SEAL_TYPE.SEALED)
                    for (BlockPos neighbor : getAdjacent(current))
                        if (!VISITED.contains(neighbor)) QUEUE.add(neighbor);
            }
        }

        if (QUEUE.isEmpty() || VISITED.size() >= getMaxVolume()) {
            finishSeal();
        }
    }

    public static SEAL_TYPE canSeal(BlockState state, Level level, BlockPos pos, BlockPos lastPos) {
        if (state.is(BlockTags.TRAPDOORS)) {
            boolean open = !state.getValue(TrapDoorBlock.OPEN);

            // stupid trapdoors. sideways and vertical open dont mean the same thing _, | -> y closed, x/z closed
            if (lastPos.getY() == pos.getY())
                open = !open;

            return open ? SEAL_TYPE.SEAL_NO_PROP : SEAL_TYPE.SEALED;
        }
        return (state.isAir() || !state.getCollisionShape(level, pos).equals(Shapes.block())) ? SEAL_TYPE.SEALED : SEAL_TYPE.UNSEALED;
    }

    protected void finishSeal() {
        ServerLevel sLevel = (ServerLevel) level;
        if (sLevel == null) return;

        var subAccess = SableCompanion.INSTANCE.getContaining(this);
        SubLevel level = null;
        if (subAccess instanceof SubLevel slevel)
            level = slevel;

        for (BlockPos oldPos : ATTACHED_POSITIONS)
            if (!VISITED.contains(oldPos))
                if (level != null)
                    OxyUtil.setBlockSealed(level, oldPos, null);
                else
                    OxyUtil.setBlockSealed(sLevel, oldPos, null);

        ATTACHED_POSITIONS.clear();
        ATTACHED_POSITIONS.addAll(VISITED);
        SYNCED_VOLUME = SEALED.size();
        RECALC = false;
        QUEUE.clear();
        SEALED.clear();

        setChanged();
        sendData();
    }

    public void unseal() {
        if (level instanceof ServerLevel slevel)
            for (BlockPos pos : ATTACHED_POSITIONS)
                OxyUtil.setBlockSealed(slevel, pos, null);
        ATTACHED_POSITIONS.clear();
        SYNCED_VOLUME = 0;
        RECALC = false;
        QUEUE.clear();
        SEALED.clear();
        setChanged();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        TANK = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true);
        TANK.getPrimaryHandler().setValidator((fluid) -> fluid.is(utmFluids.LIQUID_OXYGEN));
        CAPABILITY = new CombinedTankWrapper(TANK.getCapability());
        behaviours.add(TANK);
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

    public static List<BlockPos> getAdjacent(BlockPos pos) {
        return List.of(pos.above(), pos.below(), pos.north(), pos.east(), pos.south(), pos.west());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip, isPlayerSneaking, TANK.getCapability());
        tooltip.add(Component.empty());
        utmLang.text("Sealing Info:").style(ChatFormatting.WHITE).forGoggles(tooltip);
        utmLang.text("SEALING " + (ACTIVE ? "ACTIVE" : "INACTIVE")).style(ACTIVE ? ChatFormatting.GREEN : ChatFormatting.DARK_RED).forGoggles(tooltip);
        utmLang.text(SYNCED_VOLUME + "/" + getMaxVolume()).style(ChatFormatting.AQUA).space().add(utmLang.text("Sealed Blocks").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text(String.valueOf(getDraw() * 20)).style(ChatFormatting.AQUA).space().add(utmLang.text("Oxygen used per second").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        return true;
    }

    static {
        utmEvents.register(BlockEvent.EntityPlaceEvent.class, event -> handleWorldChange(event.getLevel(), event.getPos()));
        utmEvents.register(BlockEvent.BreakEvent.class, event -> handleWorldChange(event.getLevel(), event.getPos()));
        utmEvents.register(BlockEvent.FluidPlaceBlockEvent.class, event -> handleWorldChange(event.getLevel(), event.getPos()));
        utmEvents.register(BlockStateChangedEvent.class, event -> handleWorldChange(event.Level, event.Pos));
    }

    private static void handleWorldChange(LevelAccessor level, BlockPos pos) {
        if (!(level instanceof ServerLevel sLevel)) return;
        BlockPos controllerPos = OxyUtil.isSealed(sLevel, pos);

        if (controllerPos == null)
            for (BlockPos neighbor : List.of(pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west())) {
                controllerPos = OxyUtil.isSealed(sLevel, neighbor);
                if (controllerPos != null) break;
            }

        if (controllerPos != null && sLevel.getBlockEntity(controllerPos) instanceof AbstractSealerBlockEntity be)
            if (be.ACTIVE) {
                be.seal();

                List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(8.0));
                for (ServerPlayer player : players)
                    OxyUtil.giveTemporaryAir(player, 5 * 20); // turns out making it work with trapdoors also means you will accidentally suffocate yourself often. Oops.
            }
    }
}