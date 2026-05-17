package com.nadia.utm.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class AntiwaterBucketItem extends BucketItem {
    public AntiwaterBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, Entity entity, int slotId, boolean isSelected) {
        if (!entity.level().isClientSide) {
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), (float)8, Level.ExplosionInteraction.TNT);
            stack.shrink(1);
        }
    }
}
