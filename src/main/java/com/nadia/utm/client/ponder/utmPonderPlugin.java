package com.nadia.utm.client.ponder;

import com.nadia.utm.client.ponder.scenes.LaunchContraptionScene;
import com.nadia.utm.client.ponder.scenes.OxygenCollectorScene;
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

public class utmPonderPlugin implements PonderPlugin {
    public static final ResourceLocation A23 = utm.key("2313ag_reqs");
    public static final ResourceLocation OXYGEN = utm.key("oxygen");

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(utmBlocks.LAUNCH_CONTRAPTION.block.getId())
                .addStoryBoard("launch_contraption/launch_contraption", LaunchContraptionScene::scene, A23);

        helper.forComponents(utmBlocks.OXYGEN_COLLECTOR.block.getId())
                .addStoryBoard("oxygen_collector/oxygen_collector", OxygenCollectorScene::scene, A23, OXYGEN);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<DeferredHolder<?, ?>> entryHelper = helper.withKeyFunction(DeferredHolder::getId);

        helper.registerTag(A23)
                .addToIndex()
                .item(utmBlocks.LAUNCH_CONTRAPTION.item.get(), true, false)
                .title("2313AG Components")
                .description("Required components to interact with/enter 2313AG")
                .register();

        helper.registerTag(OXYGEN)
                .addToIndex()
                .item(utmBlocks.OXYGEN_COLLECTOR.item.get(), true, false)
                .title("Oxygen Manipulation")
                .description("Blocks that work with Liquid Oxygen")
                .register();

        entryHelper.addToTag(A23).add(utmBlocks.LAUNCH_CONTRAPTION.block);
        entryHelper.addToTag(A23).add(utmBlocks.OXYGEN_COLLECTOR.block);
        entryHelper.addToTag(OXYGEN).add(utmBlocks.OXYGEN_COLLECTOR.block);
        entryHelper.addToTag(AllCreatePonderTags.CONTRAPTION_ACTOR).add(utmBlocks.LAUNCH_CONTRAPTION.block);
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
