package com.nadia.utm.client.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

public class OxygenFurnaceScene {
    public static void scene(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("oxygen_furnace.intro", "Creating Steel");
        CreateSceneBuilder.WorldInstructions world = scene.world();
        scene.configureBasePlate(0, 0, 6);

        for (int layer = 0; layer < 6; layer++) {
            world.showSection(util.select().layer(layer), Direction.DOWN);
            scene.idle(1);
        }
        scene.idle(10);
        world.setKineticSpeed(util.select().layersFrom(0), 64);
    }
}
