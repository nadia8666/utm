package com.nadia.utm.client.ponder.scenes;

import com.nadia.utm.registry.fluid.utmFluids;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class OxygenCollectorScene {
    public static void scene(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("oxygen_collector.intro", "Collecting Oxygen");
        CreateSceneBuilder.WorldInstructions world = scene.world();
        scene.configureBasePlate(0, 0, 6);

        for (int layer = 0; layer < 6; layer++) {
            world.showSection(util.select().layer(layer), Direction.DOWN);
            scene.idle(1);
        }
        scene.idle(10);
        world.setKineticSpeed(util.select().layersFrom(0), 128);

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("The Oxygen Collector condenses oxygen from the surrounding area")
                .placeNearTarget()
                .pointAt(util.vector().topOf(3, 2, 1));

        scene.idle(120);

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("You can pump LOX from its internal tank")
                .placeNearTarget()
                .pointAt(util.vector().topOf(3, 2, 1));

        for (int i = 0; i < 16*3; i++) {
            world.modifyBlockEntity(util.grid().at(2, 2, 5), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(
                    new FluidStack(utmFluids.LIQUID_OXYGEN, 1000/3), IFluidHandler.FluidAction.EXECUTE
            ));
            scene.idle(1);
        }
        for (int i = 0; i < 16*3; i++) {
            world.modifyBlockEntity(util.grid().at(2, 3, 5), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(
                    new FluidStack(utmFluids.LIQUID_OXYGEN, 1000/3), IFluidHandler.FluidAction.EXECUTE
            ));
            scene.idle(1);
        }

        scene.idle(24);

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("On planets without oxygen the collector can be surrounded in up to 7³ leaves")
                .placeNearTarget()
                .pointAt(util.vector().topOf(3, 2, 1));

        scene.idle(120);

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("The more leaves and the higher the RPM, the more efficient it is!")
                .placeNearTarget()
                .pointAt(util.vector().topOf(3, 2, 1));

        scene.idle(120);

        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("* This functionality is shared with the backtank")
                .placeNearTarget()
                .pointAt(util.vector().topOf(3, 2, 1));

        scene.idle(80);
    }
}
