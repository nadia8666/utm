package com.nadia.utm.client.ponder;

import com.nadia.utm.client.ponder.scenes.LaunchContraptionScene;
import com.nadia.utm.client.ponder.scenes.OxygenCollectorScene;
import com.nadia.utm.client.ponder.scenes.OxygenFurnaceScene;
import com.nadia.utm.registry.block.utmBlockContainer;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.utm;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class utmPonderPlugin implements PonderPlugin {
    public static final ResourceLocation A23 = utm.key("2313ag_reqs");
    public static final ResourceLocation OXYGEN = utm.key("oxygen");

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(utmBlocks.LAUNCH_CONTRAPTION.BLOCK.getId())
                .addStoryBoard("launch_contraption/launch_contraption", LaunchContraptionScene::scene, A23);

        helper.forComponents(utmBlocks.OXYGEN_COLLECTOR.BLOCK.getId())
                .addStoryBoard("oxygen_collector/oxygen_collector", OxygenCollectorScene::scene, A23, OXYGEN);

        helper.forComponents(utmBlocks.OXYGEN_FURNACE.BLOCK.getId())
                .addStoryBoard("oxygen_furnace/oxygen_furnace", OxygenFurnaceScene::scene, A23, OXYGEN);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<DeferredHolder<?, ?>> entryHelper = helper.withKeyFunction(DeferredHolder::getId);

        helper.registerTag(A23)
                .addToIndex()
                .item(utmBlocks.LAUNCH_CONTRAPTION.ITEM.get(), true, false)
                .title("2313AG Components")
                .description("Required components to interact with/enter 2313AG")
                .register();

        helper.registerTag(OXYGEN)
                .addToIndex()
                .item(utmBlocks.OXYGEN_COLLECTOR.ITEM.get(), true, false)
                .title("Oxygen Manipulation")
                .description("Blocks that work with Liquid Oxygen")
                .register();

        utmBlockContainer.DATAGEN_TARGETS.forEach((c, tags) -> {
            for (String tag : tags) {
                if (tag.startsWith("ponderTags")) {
                    List<String> ponderTags = Arrays.stream(Arrays.stream(tag.split(":")).toList().getLast().split(",")).toList();
                    for (String path : ponderTags) {
                        switch (path) {
                            case "2313ag_reqs":
                                entryHelper.addToTag(A23).add(c.BLOCK);
                                break;
                            case "oxygen":
                                entryHelper.addToTag(OXYGEN).add(c.BLOCK);
                                break;
                            case "contraption_actor":
                                entryHelper.addToTag(AllCreatePonderTags.CONTRAPTION_ACTOR);
                                break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public @NotNull String getModId() {
        return utm.MODID;
    }

    @OnlyIn(Dist.CLIENT)
    public void register() {
        PonderIndex.addPlugin(this);
    }
}
