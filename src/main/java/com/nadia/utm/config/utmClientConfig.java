package com.nadia.utm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class utmClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue ALTERNATE_TAB_MENU;
    public static final ModConfigSpec.BooleanValue NETHERITE_BACKTANK_ARM;
    public static final ModConfigSpec.BooleanValue RENDER_PLANETS;
    public static final ModConfigSpec.BooleanValue FIGURA_PUNCHY;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("client_ui");
        ALTERNATE_TAB_MENU = BUILDER.define("altTabMenu", true);
        BUILDER.pop();

        BUILDER.push("client_visual");
        NETHERITE_BACKTANK_ARM = BUILDER.define("netheriteBacktankArm", true);
        RENDER_PLANETS = BUILDER.define("renderPlanets", true);
        BUILDER.pop();

        BUILDER.push("client_compat");
        FIGURA_PUNCHY = BUILDER.define("figuraPunchy", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
