package com.nadia.utm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class utmCommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue AUTO_UPDATE_ENABLED;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("common_updater");
        AUTO_UPDATE_ENABLED = BUILDER.define("autoUpdate", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
