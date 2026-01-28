package com.nadia.utm;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT = BUILDER
            .comment("Level require to reinforce an anvil into a heavy-metal anvil.")
            .defineInRange("heavyMetalAnvilLV", 10, 0, 30);

    static final ModConfigSpec SPEC = BUILDER.build();
}
