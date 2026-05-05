package com.nadia.utm.mixin.compat.sable;

import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(value = SwivelBearingBlockEntity.class, remap = false)
public abstract class SwivelBearingBlockEntityMixin implements BlockEntitySubLevelActorExtensions<SwivelBearingBlockEntity> {
    @Shadow
    public abstract void setSubLevelID(@Nullable UUID subLevelID);

    @Shadow
    public abstract void setPlatePos(@Nullable BlockPos swivelPlatePos);

    @Shadow
    public abstract void reattachConstraint(SubLevel toAttach, boolean updatePlate);

    @Override
    public boolean sable$migrateData(final Map<ServerSubLevel, ServerSubLevel> conversions, final SwivelBearingBlockEntity oldBE, final Map<ServerSubLevel, SubLevelAssemblyHelper.AssemblyTransform> transforms) {
        final BlockPos platePos = oldBE.getPlatePos();
        if (platePos != null && SableCompanion.INSTANCE.getContaining(oldBE) instanceof final ServerSubLevel oldLevel && conversions.get(oldLevel) instanceof final ServerSubLevel newLevel) {
            final ServerSubLevel plateLevel = (ServerSubLevel) Objects.requireNonNull(SubLevelContainer.getContainer(oldLevel.getLevel())).getSubLevel(oldBE.getSubLevelID()); // TODO: not this

            setSubLevelID(newLevel.getUniqueId());
            setPlatePos(transforms.get(newLevel).apply(platePos));
            reattachConstraint(conversions.get(plateLevel), true);
            return true;
        }
        return false;
    }

    public void sable$cleanLevelNBT(final CompoundTag tag) {
        tag.remove("SubLevelID");
        tag.remove("SwivelPlate");
    }
}
