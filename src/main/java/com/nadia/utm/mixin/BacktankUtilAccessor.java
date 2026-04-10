package com.nadia.utm.mixin;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.simibubi.create.content.equipment.armor.BacktankUtil.getAir;
import static com.simibubi.create.content.equipment.armor.BacktankUtil.hasAirRemaining;

@Mixin(value = BacktankUtil.class, remap = false)
public interface BacktankUtilAccessor {
    @Accessor(value = "BACKTANK_SUPPLIERS")
    static List<Function<LivingEntity, List<ItemStack>>> getSuppliers() {
        throw new UnsupportedOperationException();
    }
}
