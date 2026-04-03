package com.nadia.utm.registry.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.block.Blocks;

import java.util.Vector;

public class LaunchContraptionScene {
    public static void basic(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("launch_contraption.intro", "Using the Launch Contraption");
        CreateSceneBuilder.WorldInstructions world = scene.world();
        scene.configureBasePlate(0, 0, 5);
        world.showSection(util.select().layer(0), Direction.DOWN);
        scene.idle(10);
        var cart = scene.special().createCart(util.vector().topOf(2, 0, 0), 90f, Minecart::new);
        scene.special().moveCart(cart, util.vector().of(0, 0, 2), 20);
        world.showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(10);
        world.showSection(util.select().layer(2), Direction.DOWN);
        scene.idle(10);
        world.showSection(util.select().layer(3), Direction.DOWN);
        var birb = scene.special().createBirb(util.vector().of(2.5, 3.5, 1.5), ParrotPose.FlappyPose::new);

        Selection toggle = util.select().fromTo(util.grid().at(1, 1, 2), util.grid().at(2, 1, 2));

        scene.overlay().showText(100)
                .attachKeyFrame()
                .text("The Launch Contraption is the core component required to leave for space")
                .placeNearTarget()
                .pointAt(util.vector().topOf(2, 3, 3));
        scene.idle(120);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("When attached to a minecart contraption, it gains a functional interface")
                .placeNearTarget()
                .pointAt(util.vector().topOf(1, 1, 2));
        scene.idle(20);
        scene.overlay().showOutline(PonderPalette.GREEN, "glue", util.select().position(2, 2, 1)
                .add(util.select().fromTo(2, 2, 1, 2, 3, 3))
                .add(util.select().position(2, 3, 3)), 20);
        scene.overlay().showControls(util.vector().centerOf(util.grid().at(2, 3, 3)), Pointing.RIGHT, 40)
                .withItem(AllItems.SUPER_GLUE.asStack());
        scene.idle(10);

        world.toggleRedstonePower(toggle);
        scene.effects().indicateRedstone(util.grid().at(1, 1, 2));
        scene.idle(80);

        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("Pressing the buttom will launch the contraption into space")
                .placeNearTarget()
                .pointAt(util.vector().topOf(2, 3, 3));

        scene.idle(60);

        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("All entities sat on the contraption will be transported with it")
                .placeNearTarget()
                .pointAt(util.vector().topOf(2, 3, 3));


        scene.overlay().showOutline(PonderPalette.WHITE, "cursor", util.select().position(2, 3, 1), 20);

        scene.idle(80);

        scene.overlay().showText(20)
                .attachKeyFrame()
                .text("Goodbye parrot!")
                .placeNearTarget()
                .pointAt(util.vector().topOf(2, 3, 3));

        scene.idle(20);

        world.setBlocks(util.select().fromTo(2,2, 1, 2, 3, 3), Blocks.AIR.defaultBlockState(), false);
        scene.special().moveCart(cart, util.vector().of(0,-500,0), 0);
        scene.special().moveParrot(birb, util.vector().of(0, -500, 0), 0);

        scene.idle(40);
    }
}
