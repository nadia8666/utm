package com.nadia.utm.mixin.compat.sable;

import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(value = SpringBlockEntity.class, remap = false)
public abstract class SpringBlockEntityMixin implements BlockEntitySubLevelActorExtensions<SpringBlockEntity> {
    @Shadow
    public abstract void setPartnerPos(BlockPos pos, UUID subLevel);

    @Override
    public boolean sable$migrateData(final Map<ServerSubLevel, ServerSubLevel> conversions, final SpringBlockEntity oldBE, final Map<ServerSubLevel, SubLevelAssemblyHelper.AssemblyTransform> transforms) {
        final SpringBlockEntity pair = oldBE.getPairedSpring();
        final UUID partnerID = oldBE.getPartnerSubLevelID();

        if (pair != null && partnerID != null) {
            final ServerSubLevel newLevel = conversions.entrySet().stream()
                    .filter((e) -> e.getKey().getUniqueId().equals(partnerID))
                    .map(Map.Entry::getValue).findFirst().orElseThrow();

            setPartnerPos(transforms.get(newLevel).apply(pair.getBlockPos()), newLevel.getUniqueId());
            return true;
        }

        return false;
    }

    public void sable$cleanLevelNBT(final CompoundTag tag) {
        tag.remove("GoalSubLevel");
        tag.remove("Goal");
    }
}
