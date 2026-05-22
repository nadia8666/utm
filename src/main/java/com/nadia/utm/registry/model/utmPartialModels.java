package com.nadia.utm.registry.model;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.utm;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.neoforged.api.distmarker.Dist;

import java.util.HashMap;
import java.util.Map;

@ForceLoad(dist = Dist.CLIENT)
public class utmPartialModels {
    public static final PartialModel OXYGEN_FAN = PartialModel.of(utm.key("block/oxygen_fan"));
    public static final PartialModel OXYGEN_COLLECTOR_GRILL = PartialModel.of(utm.key("block/oxygen_collector_grill"));
    public static final PartialModel OXYGEN_FURNACE_TANKS = PartialModel.of(utm.key("block/oxygen_furnace_tanks"));
    public static final PartialModel BIOME_SEALER_GRILL = PartialModel.of(utm.key("block/biome_sealer_grill"));

    // TODO: refactor this lol
    public static final PartialModel COPPER_THROWING_SPEAR = PartialModel.of(utm.key("item/copper_throwing_spear"));
    public static final PartialModel NETHERITE_THROWING_SPEAR = PartialModel.of(utm.key("item/netherite_throwing_spear"));
    public static final Map<String, PartialModel> THROWING_SPEAR_MODELS = new HashMap<>();

    static {
        THROWING_SPEAR_MODELS.put("copper_throwing_spear", COPPER_THROWING_SPEAR);
        THROWING_SPEAR_MODELS.put("netherite_throwing_spear", NETHERITE_THROWING_SPEAR);
    }
}
