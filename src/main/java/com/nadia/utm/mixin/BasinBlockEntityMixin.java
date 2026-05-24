package com.nadia.utm.mixin;

import com.nadia.utm.block.misc.large_basin.LargeBasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
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

}
