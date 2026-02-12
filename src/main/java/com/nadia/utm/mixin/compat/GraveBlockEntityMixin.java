package com.nadia.utm.mixin.compat;

import com.nadia.utm.compat.GraveInterface;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GraveStoneTileEntity.class, remap = false)
public class GraveBlockEntityMixin implements GraveInterface {
    @Unique
    public ThreadLocal<Boolean> utm$skipItems = ThreadLocal.withInitial(() -> false);
}
