package com.nadia.utm.mixin;

import com.nadia.utm.event.events.BlockStateChangedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Level.class, remap = false)
public abstract class LevelMixin {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"))
    private void utm$onChange(BlockPos pos, BlockState newState, int flags, int recursion, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        if (level.isClientSide) return;

        BlockState oldState = level.getBlockState(pos);
        if (oldState != newState)
            NeoForge.EVENT_BUS.post(new BlockStateChangedEvent(level, pos, oldState, newState));
    }
}