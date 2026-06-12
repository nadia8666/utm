package com.nadia.utm.mixin.compat.create;

import com.nadia.utm.compat.IBasinWithHeatGetter;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BasinBlockEntity.class, remap = false)
public interface BasinBlockEntityMixin extends IBasinWithHeatGetter {
    @Invoker("getHeatLevel")
    BlazeBurnerBlock.HeatLevel utm$getHeatLevel();
}
