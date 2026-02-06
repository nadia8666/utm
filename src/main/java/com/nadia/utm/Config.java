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

    static final ModConfigSpec SPEC = BUILDER.build();
}
