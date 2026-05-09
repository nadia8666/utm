package com.nadia.utm.block.misc.gimbal;

import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.block.utmBlocks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GimbalPlateBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelActorExtensions<GimbalPlateBlockEntity> {
    public BlockPos PARENT;
    public UUID PARENT_LEVEL;
    public boolean ASSEMBLING = false;

    public GimbalPlateBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.GIMBAL_PLATE.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {
    }

    @Override
    public @Nullable Iterable<@NotNull SubLevel> sable$getConnectionDependencies() {
        if (PARENT == null)
            return null;

        final SubLevelContainer container = SubLevelContainer.getContainer(this.level);

        if (PARENT_LEVEL != null) {
            assert container != null;
            final SubLevel subLevel = container.getSubLevel(PARENT_LEVEL);

            if (subLevel != null)
                return List.of(subLevel);
        }

        return null;
    }

    public void beforeAssembly() {
        ASSEMBLING = true;
    }

    @Override
    public void remove() {
        assert this.level != null;
        if (!this.level.isClientSide && !ASSEMBLING)
            this.destroyBearing();

        super.remove();
    }

    private void destroyBearing() {
        if (PARENT != null) {
            assert this.getLevel() != null;
            if (this.getLevel().getBlockState(PARENT).is(utmBlocks.GIMBAL.BLOCK)) {
                this.getLevel().destroyBlock(PARENT, false);
            }
        }
    }

    public void setParent(final GimbalBlockEntity be) {
        final SubLevel subLevel = Sable.HELPER.getContaining(be);

        this.PARENT = be.getBlockPos();
        this.PARENT_LEVEL = subLevel != null ? subLevel.getUniqueId() : null;
    }

    @Override
    public void sable$cleanLevelNBT(CompoundTag tag) {
        tag.remove("ParentPos");
        tag.remove("ParentSubLevelId");
    }

    @Override
    protected void write(final CompoundTag compound, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        if (PARENT != null)
            compound.put("ParentPos", NbtUtils.writeBlockPos(PARENT));

        if (PARENT_LEVEL != null)
            compound.putUUID("ParentSubLevelId", PARENT_LEVEL);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    protected void read(final CompoundTag compound, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        if (compound.contains("ParentPos"))
            PARENT = NbtUtils.readBlockPos(compound, "ParentPos").get();

        if (compound.contains("ParentSubLevelId"))
            PARENT_LEVEL = compound.getUUID("ParentSubLevelId");
    }

    public void fixParentLinkingWhenMoved() {
        assert this.level != null;
        if (this.level.isClientSide() || PARENT == null) {
            return;
        }

        final BlockEntity be = this.level.getBlockEntity(PARENT);

        if (be instanceof final GimbalBlockEntity gimbal) {
            gimbal.PLATE_POS = this.getBlockPos();

            final SubLevel newSublevel = Sable.HELPER.getContaining(this);
            if (newSublevel != null) {
                final UUID subLevelID = gimbal.ATTACHED_LEVEL;
                final UUID newID = newSublevel.getUniqueId();

                if (newID != subLevelID) {
                    gimbal.ATTACHED_LEVEL = newSublevel.getUniqueId();
                    gimbal.reattachConstraint(newSublevel, true);
                }
            }
        }
    }
}
