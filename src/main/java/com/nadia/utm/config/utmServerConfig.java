package com.nadia.utm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class utmServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT;
    public static final ModConfigSpec.IntValue ION_THRUSTER_FORCE;
    public static final ModConfigSpec.IntValue LIQUID_THRUSTER_FORCE;
    public static final ModConfigSpec.IntValue SOLID_THRUSTER_FORCE;
    public static final ModConfigSpec.IntValue ELYTRA_DECAY_TIME;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("server_balance");
        HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT = BUILDER.defineInRange("heavyMetalAnvilLV", 10, 0, 30);
        ELYTRA_DECAY_TIME = BUILDER.defineInRange("elytraDecayTime", 20, 0, 60);
        BUILDER.pop();

        BUILDER.push("server_physics");
        ION_THRUSTER_FORCE = BUILDER.defineInRange("ionThrusterForce", 125, 0, 1500);
        LIQUID_THRUSTER_FORCE = BUILDER.defineInRange("liquidThrusterForce", 850, 0, 4000);
        SOLID_THRUSTER_FORCE = BUILDER.defineInRange("solidThrusterForce", 1670, 0, 14000);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
