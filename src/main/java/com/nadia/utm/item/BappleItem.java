package com.nadia.utm.item;

import com.nadia.utm.registry.sound.utmSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BappleItem extends Item {
    public BappleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        if (!level.isClientSide) {
            level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), utmSounds.BAPPLE, livingEntity.getSoundSource(), 3f, 1f);
            livingEntity.hurt(level.damageSources().dryOut(), 5f);
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }
}
