package com.nadia.utm.mixin;

import com.nadia.utm.block.misc.large_basin.LargeBasinBlockEntity;
import com.nadia.utm.mixin.compat.create.BasinRecipeMixin;
import com.nadia.utm.registry.fluid.utmFluids;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = BasinBlockEntity.class, remap = false)

//BasinOperatingBlockEntity ??

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
        int tnAntiwater = 0;
        int tnWater = 0;
        int tnExperiecne = 0;
        for (SmartFluidTankBehaviour tank : basin.getTanks()) {
            IFluidHandler capability = tank.getCapability();
            for (int i = 0; i < capability.getTanks(); i++) {
                FluidStack stack = capability.getFluidInTank(i);
                if (stack.is(utmFluids.ANTIWATER)) {
                    tnAntiwater += stack.getAmount(); //plus equal because there will inevitably be Some old poor old basin with
                    //some water in output and Stuf.
                } else if (stack.is(Fluids.WATER)) {
                    tnWater += stack.getAmount();

                } else if (stack.is(AllFluids.CHOCOLATE)) { // tough find!
                    tnExperiecne += stack.getAmount(); //..not experience
                }
            }

        }
        if (tnAntiwater > 0) {
            boolean lb = (basin instanceof LargeBasinBlockEntity);
            boolean cooled = (tnExperiecne > (tnWater*2)+tnAntiwater);
            boolean wp = (tnWater > 0);
            int finalChance = Mth.floor(1000 * (lb ? 10 : 1) * (cooled ? 30 : 1) / (wp ? 0.5 : 1));
            int rand = basin.getLevel().getRandom().nextIntBetweenInclusive(1, finalChance);
            // if water present reduce to 10
            if (rand == 1) {
                basin.getLevel().explode(null, basin.getBlockPos().getX() + 0.5D, basin.getBlockPos().getY() + 0.5D, basin.getBlockPos().getZ() + 0.5D, 12F, true, Level.ExplosionInteraction.BLOCK);
                return;
            }
        }
    }
}
