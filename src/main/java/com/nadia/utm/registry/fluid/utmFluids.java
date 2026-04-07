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

    // liquid oxygen
    public static final DeferredHolder<FluidType, FluidType> LIQUID_OXYGEN_TYPE = FLUID_TYPES.register("liquid_oxygen",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.utm.liquid_oxygen")
                    .fallDistanceModifier(2f)
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
            .bucket(utmItems.LIQUID_OXYGEN_BUCKET.ITEM())
            .block(utmBlocks.LIQUID_OXYGEN_BLOCK);

    // molten steel
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_STEEL_TYPE = FLUID_TYPES.register("molten_steel",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.utm.molten_steel")
                    .density(1450)
                    .viscosity(150)
                    .temperature(1800)
                    .rarity(Rarity.RARE)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MOLTEN_STEEL = FLUIDS.register("molten_steel",
            () -> new BaseFlowingFluid.Source(utmFluids.MOLTEN_STEEL_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MOLTEN_STEEL = FLUIDS.register("flowing_molten_steel",
            () -> new BaseFlowingFluid.Flowing(utmFluids.MOLTEN_STEEL_PROPERTIES));

    protected static final BaseFlowingFluid.Properties MOLTEN_STEEL_PROPERTIES = new BaseFlowingFluid.Properties(
            MOLTEN_STEEL_TYPE, MOLTEN_STEEL, FLOWING_MOLTEN_STEEL)
            .bucket(utmItems.MOLTEN_STEEL_BUCKET.ITEM())
            .block(utmBlocks.MOLTEN_STEEL_BLOCK);
}
