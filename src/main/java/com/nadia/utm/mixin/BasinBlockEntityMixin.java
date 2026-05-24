package com.nadia.utm.mixin;

import com.nadia.utm.block.misc.large_basin.LargeBasinBlockEntity;
import com.nadia.utm.registry.fluid.utmFluids;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {
    @ModifyArgs(
            method = "addBehaviours",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/fluid/SmartFluidTankBehaviour;<init>(Lcom/simibubi/create/foundation/blockEntity/behaviour/BehaviourType;Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;IIZ)V"
            )
    )
    private void utm$largeBasinsControlTheWorld(Args args) {
        BasinBlockEntity basin = (BasinBlockEntity) (Object) this;

        if (basin instanceof LargeBasinBlockEntity) {
            args.set(2, 10);
            args.set(3, 10000);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void utm$tick(CallbackInfo ci) {
        BasinBlockEntity basin = (BasinBlockEntity) (Object) this;
        if (basin.getLevel() == null || basin.getLevel().isClientSide) return;

        for (SmartFluidTankBehaviour tank : basin.getTanks()) {
            IFluidHandler capability = tank.getCapability();
            for (int i = 0; i < capability.getTanks(); i++) {
                FluidStack stack = capability.getFluidInTank(i);
                if (stack.is(utmFluids.ANTIWATER)) {
                    int rand = basin.getLevel().getRandom().nextIntBetweenInclusive(1, basin instanceof LargeBasinBlockEntity ? 6666 : 666);

                    if (rand == 1) {
                        basin.getLevel().explode(null, basin.getBlockPos().getX() + 0.5D, basin.getBlockPos().getY() + 0.5D, basin.getBlockPos().getZ() + 0.5D, 12F, true, Level.ExplosionInteraction.BLOCK);
                        return;
                    }
                }
            }
        }
    }
}
