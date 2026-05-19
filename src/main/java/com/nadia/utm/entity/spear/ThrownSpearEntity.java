package com.nadia.utm.entity.spear;

import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ThrownSpearEntity extends AbstractArrow {
    public ThrownSpearEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(utmTools.COPPER_THROWING_SPEAR.get());
    }
}
