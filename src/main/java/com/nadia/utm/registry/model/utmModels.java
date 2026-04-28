package com.nadia.utm.registry.model;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.utm;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.neoforged.api.distmarker.Dist;

@ForceLoad(dist = Dist.CLIENT)
public class utmModels {
    public static final PartialModel OXYGEN_FAN = PartialModel.of(utm.key("block/oxygen_fan"));
    public static final PartialModel OXYGEN_COLLECTOR_GRILL = PartialModel.of(utm.key("block/oxygen_collector_grill"));
    public static final PartialModel OXYGEN_FURNACE_TANKS = PartialModel.of(utm.key("block/oxygen_furnace_tanks"));
    public static final PartialModel BIOME_SEALER_GRILL = PartialModel.of(utm.key("block/biome_sealer_grill"));
    public static final PartialModel AEROWALL = PartialModel.of(utm.key("block/aerowall_mesh"));
}
