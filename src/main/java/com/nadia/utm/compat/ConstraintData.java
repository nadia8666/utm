package com.nadia.utm.compat;

import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;

public record ConstraintData<T extends PhysicsConstraintHandle>(ServerSubLevel sublevelA, ServerSubLevel sublevelB,
                                                                T constraint, PhysicsConstraintConfiguration<?> config) {
}
