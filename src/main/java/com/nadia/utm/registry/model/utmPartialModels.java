package com.nadia.utm.registry.model;

import com.nadia.utm.utm;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class utmPartialModels {
    public static final PartialModel OXYGEN_FAN = PartialModel.of(utm.key("block/oxygen_fan"));
    public static final PartialModel OXYGEN_COLLECTOR_GRILL = PartialModel.of(utm.key("block/oxygen_collector_grill"));

    public static void register() {}
}
