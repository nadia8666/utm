package com.nadia.utm.item;

import com.nadia.utm.entity.projectile.AridStoneEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AridIngotItem extends Item {
    public AridIngotItem(Properties properties) {
        super(properties);
    }
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        AridStoneEntity snowball = new AridStoneEntity(level, pos.x(), pos.y(), pos.z());
        snowball.setItem(stack);
        return snowball;
    }
}
