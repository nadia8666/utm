package com.nadia.utm.compat;

public interface GraveInterface {
    ThreadLocal<Boolean> utm$skipItems = ThreadLocal.withInitial(() -> false);
}
