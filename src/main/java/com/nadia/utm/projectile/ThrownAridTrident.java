package com.nadia.utm.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownAridTrident extends ThrownTrident {
    public ThrownAridTrident(EntityType<? extends ThrownTrident> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownAridTrident(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
        super(level, shooter, pickupItemStack);
    }

    public ThrownAridTrident(Level level, double x, double y, double z, ItemStack pickupItemStack) {
        super(level, x, y, z, pickupItemStack);
    }
}
