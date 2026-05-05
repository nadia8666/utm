package com.nadia.utm.mixin.compat.sable;

import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.link_block.SwivelBearingPlateBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SwivelBearingPlateBlockEntity.class, remap = false)
public class SwivelBearingPlateBlockEntityMixin implements BlockEntitySubLevelActorExtensions<SwivelBearingPlateBlockEntity> {
    public void sable$cleanLevelNBT(final CompoundTag tag) {
        tag.remove("ParentPos");
        tag.remove("ParentSubLevelId");
    }
}
