package com.nadia.utm.mixin.compat.sable;

import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(value = DockingConnectorBlockEntity.class, remap = false)
public abstract class DockingConnectorBlockEntityMixin implements BlockEntitySubLevelActorExtensions<DockingConnectorBlockEntity> {
    @Unique
    private static Set<DockingConnectorBlockEntity> utm$usedDocks = new HashSet<>();

    @Shadow
    public abstract void pairTo(DockingConnectorBlockEntity other);

    @Override
    public boolean sable$migrateData(Map<ServerSubLevel, ServerSubLevel> conversions, DockingConnectorBlockEntity oldBE, Map<ServerSubLevel, SubLevelAssemblyHelper.AssemblyTransform> transforms) {
        if (oldBE.hasOtherConnector() && oldBE.getOtherConnector() instanceof DockingConnectorBlockEntity other && SableCompanion.INSTANCE.getContaining(other) instanceof ServerSubLevel oldLevel) {
            if (utm$usedDocks.remove(oldBE)) return false;

            utm$usedDocks.add(other);

            ServerSubLevel nextLevel = conversions.get(oldLevel);
            pairTo((DockingConnectorBlockEntity) nextLevel.getLevel().getBlockEntity(transforms.get(nextLevel).apply(other.getBlockPos())));
            return true;
        }

        return false;
    }

    public void sable$cleanLevelNBT(CompoundTag tag) {
        tag.remove("OtherConnector");
        tag.remove("OtherConnectorSubLevelId");
    }
}
