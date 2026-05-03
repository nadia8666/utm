package com.nadia.utm.mixin.compat.sable;

import com.nadia.utm.compat.ConstraintData;
import com.nadia.utm.compat.IConstraintAccessor;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.physics.impl.rapier.RapierPhysicsPipeline;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RapierPhysicsPipeline.class, remap = false)
public abstract class RapierPhysicsPipelineMixin implements IConstraintAccessor {
    @Unique
    private List<ConstraintData<?>> utm$constraints = new ArrayList<>();

    @Unique
    public List<ConstraintData<?>> utm$getForLevel(ServerSubLevel target) {
        List<ConstraintData<?>> out = new ArrayList<>();
        utm$constraints.removeIf((constraint) -> constraint.sublevelA() == null || constraint.sublevelB() == null || constraint.constraint() == null);

        for (ConstraintData<?> constraint : utm$constraints) {
            if (constraint.sublevelA() == target || constraint.sublevelB() == target)
                out.add(constraint);
        }

        return out;
    }

    @Unique
    public List<ConstraintData<?>> utm$getConstraints() {
        return utm$constraints;
    }

    @Inject(method = "addConstraint", at = @At("RETURN"))
    private <T extends PhysicsConstraintHandle> void utm$addConstraint(@Nullable ServerSubLevel sublevelA, @Nullable ServerSubLevel sublevelB, PhysicsConstraintConfiguration<T> configuration, CallbackInfoReturnable<T> cir) {
        T constraint = cir.getReturnValue();
        if (constraint != null)
            utm$constraints.add(new ConstraintData<>(sublevelA, sublevelB, constraint, configuration));
    }
}
