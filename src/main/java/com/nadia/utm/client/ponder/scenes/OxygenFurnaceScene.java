package com.nadia.utm.client.ponder.scenes;

import com.nadia.utm.block.entity.OxygenFurnaceBlockEntity;
import com.nadia.utm.registry.fluid.utmFluids;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class OxygenFurnaceScene {
    public static void scene(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("oxygen_furnace.intro", "Creating Steel");
        CreateSceneBuilder.WorldInstructions world = scene.world();
        scene.configureBasePlate(0, 0, 6);

        world.setKineticSpeed(util.select().layersFrom(0), 64);
        for (int layer = 0; layer < 6; layer++) {
            world.showSection(util.select().layer(layer), Direction.DOWN);
            scene.idle(1);
        }
        scene.idle(10);

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("The Oxygen Furnace produces molten steel when provided with the right inputs")
                .placeNearTarget()
                .pointAt(util.vector().topOf(2,1,3));

        scene.idle(120);

        BlockPos furnace = util.grid().at(2,1,3);
        scene.overlay().showControls(util.select().position(furnace.west()).getCenter(), Pointing.LEFT, 30)
                .withItem(new ItemStack(Items.COAL));
        scene.overlay().showControls(util.select().position(furnace.above()).getCenter(), Pointing.DOWN, 30)
                .withItem(new ItemStack(Items.IRON_INGOT));

        scene.idle(40);

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("The Oxygen Furnace requires coal, iron, and LOX")
                .placeNearTarget()
                .pointAt(util.vector().topOf(2,1,3));

        scene.idle(120);

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("The resulting molten steel can then be pressed into steel ingots and sheets")
                .placeNearTarget()
                .pointAt(util.vector().topOf(2,1,3));

        world.modifyBlockEntity(furnace, OxygenFurnaceBlockEntity.class, be -> be.STEEL.getPrimaryHandler().fill(
                new FluidStack(utmFluids.MOLTEN_STEEL, 500), IFluidHandler.FluidAction.EXECUTE
        ));

        world.modifyBlockEntity(furnace, OxygenFurnaceBlockEntity.class, be -> be.LOX.getPrimaryHandler().drain(
                500, IFluidHandler.FluidAction.EXECUTE
        ));

        for (int i = 0; i < 16*3; i++) {
            world.modifyBlockEntity(util.grid().at(5, 1, 3), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(
                    new FluidStack(utmFluids.MOLTEN_STEEL, 1000/3), IFluidHandler.FluidAction.EXECUTE
            ));
            scene.idle(1);
        }
        for (int i = 0; i < 16*3; i++) {
            world.modifyBlockEntity(util.grid().at(5, 1, 3), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(
                    new FluidStack(utmFluids.MOLTEN_STEEL, 1000/3), IFluidHandler.FluidAction.EXECUTE
            ));
            scene.idle(1);
        }
        for (int i = 0; i < 16*3; i++) {
            world.modifyBlockEntity(util.grid().at(5, 1, 3), FluidTankBlockEntity.class, be -> be.getTankInventory().fill(
                    new FluidStack(utmFluids.MOLTEN_STEEL, 1000 / 3), IFluidHandler.FluidAction.EXECUTE
            ));
            scene.idle(1);
        }

        scene.idle(20);
    }
}
