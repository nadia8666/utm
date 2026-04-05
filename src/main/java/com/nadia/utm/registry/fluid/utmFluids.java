package com.nadia.utm.registry.fluid;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class utmFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, "utm");
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, "utm");

    public static final DeferredHolder<FluidType, FluidType> LIQUID_OXYGEN_TYPE = FLUID_TYPES.register("liquid_oxygen",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.utm.liquid_oxygen")
                    .fallDistanceModifier(0f)
                    .canExtinguish(true)
                    .density(1150)
                    .viscosity(200)
                    .temperature(90)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_OXYGEN = FLUIDS.register("liquid_oxygen",
            () -> new BaseFlowingFluid.Source(utmFluids.LIQUID_OXYGEN_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_LIQUID_OXYGEN = FLUIDS.register("flowing_liquid_oxygen",
            () -> new BaseFlowingFluid.Flowing(utmFluids.LIQUID_OXYGEN_PROPERTIES));

    protected static final BaseFlowingFluid.Properties LIQUID_OXYGEN_PROPERTIES = new BaseFlowingFluid.Properties(
            LIQUID_OXYGEN_TYPE, LIQUID_OXYGEN, FLOWING_LIQUID_OXYGEN)
            .bucket(utmItems.LIQUID_OXYGEN_BUCKET)
            .block(utmBlocks.LIQUID_OXYGEN_BLOCK);
}
