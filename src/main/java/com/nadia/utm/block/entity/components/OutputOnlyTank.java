package com.nadia.utm.block.entity.components;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class OutputOnlyTank extends FluidTank {
    public OutputOnlyTank(int maxAir) {
        super(maxAir);
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return 0;
    }

    public void forceFill(FluidStack resource, FluidAction action) {
        super.fill(resource, action);
    }
}
