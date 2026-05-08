package com.nadia.utm;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue AUTO_UPDATE_ENABLED = BUILDER
            .define("autoUpdate", true);

    public static final ModConfigSpec.IntValue HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT = BUILDER
            .defineInRange("heavyMetalAnvilLV", 10, 0, 30);

    public static final ModConfigSpec.BooleanValue ALTERNATE_TAB_MENU = BUILDER
            .define("altTabMenu", true);

    public static final ModConfigSpec.BooleanValue NETHERITE_BACKTANK_ARM = BUILDER
            .define("netheriteBacktankArm", true);

    public static final ModConfigSpec.BooleanValue RENDER_PLANETS = BUILDER
            .define("renderPlanets", true);

    public static final ModConfigSpec.BooleanValue FIGURA_PUNCHY = BUILDER
            .define("figuraPunchy", true);

    public static final ModConfigSpec.IntValue ION_THRUSTER_FORCE = BUILDER
            .defineInRange("ionThrusterForce", 125, 0, 1500);

    public static final ModConfigSpec.IntValue LIQUID_THRUSTER_FORCE = BUILDER
            .defineInRange("liquidThrusterForce", 850, 0, 4000);

    public static final ModConfigSpec.IntValue SOLID_THRUSTER_FORCE = BUILDER
            .defineInRange("solidThrusterForce", 1670, 0, 14000);

    static final ModConfigSpec SPEC = BUILDER.build();
}
