package com.nadia.utm.mixin.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.pac.common.server.api.OpenPACServerAPI;

import java.util.Optional;

@Mixin(value = PumpUpgradeWrapper.class, remap = false)
public class PumpUpgradeWrapperMixin {
    @Inject(
            method = "interactWithWorld",
            at= @At(value = "INVOKE", target = "Lnet/p3pp3rf1y/sophisticatedcore/upgrades/pump/PumpUpgradeWrapper;placeFluidInWorld(Lnet/minecraft/world/level/Level;Lnet/neoforged/neoforge/fluids/capability/IFluidHandler;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z"),
            cancellable = true
    )
    public void utm$compatPumpProtection(Level level, BlockPos pos, IFluidHandler storageFluidHandler, Entity entity, CallbackInfoReturnable<Optional<Integer>> cir) {
         if (!(level instanceof ServerLevel serverLevel)) return;
         if (FMLEnvironment.dist != Dist.DEDICATED_SERVER) return;

         var opac = OpenPACServerAPI.get(serverLevel.getServer());
         var blocked = opac.getChunkProtection().onBlockInteraction(entity, InteractionHand.MAIN_HAND, null, serverLevel, pos, Direction.UP, false, true, false);
         if (blocked) cir.cancel();
    }
}
