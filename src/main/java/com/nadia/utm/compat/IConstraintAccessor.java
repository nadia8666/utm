package com.nadia.utm.compat;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;

import java.util.List;

public interface IConstraintAccessor {
    List<ConstraintData<?>> utm$getForLevel(ServerSubLevel target);

    List<ConstraintData<?>> utm$getConstraints();
}
