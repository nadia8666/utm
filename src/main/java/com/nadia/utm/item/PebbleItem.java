package com.nadia.utm.item;

import com.nadia.utm.entity.projectile.AridStoneEntity;
import com.nadia.utm.entity.projectile.PebbleEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PebbleItem extends Item {
    public PebbleItem(Properties properties) {
        super(properties);
    }
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        PebbleEntity snowball = new PebbleEntity(level, pos.x(), pos.y(), pos.z());
        snowball.setItem(stack);
        return snowball;
    }
}
