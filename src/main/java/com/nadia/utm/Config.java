package com.nadia.utm;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue AUTO_UPDATE_ENABLED = BUILDER
            .comment("Should utm automatically update.")
            .define("autoUpdate", true);

    public static final ModConfigSpec.IntValue HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT = BUILDER
            .comment("Level require to reinforce an anvil into a heavy-metal anvil.")
            .defineInRange("heavyMetalAnvilLV", 10, 0, 30);

    public static final ModConfigSpec.BooleanValue ALTERNATE_TAB_MENU = BUILDER
            .comment("Should utm replace the tab menu with a more detailed varaint?")
            .define("altTabMenu", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
