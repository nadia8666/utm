package com.nadia.utm.mixin.compat.sable;

import com.nadia.utm.compat.IRapierConstraintAccessor;
import dev.ryanhcode.sable.physics.impl.rapier.constraint.RapierConstraintHandle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = RapierConstraintHandle.class, remap = false)
public interface RapierConstraintHandleMixin extends IRapierConstraintAccessor {
    @Accessor("handle")
    @Override
    long utm$getHandle();

    @Accessor("sceneID")
    @Override
    int utm$getSceneID();
}
