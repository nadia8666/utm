package com.nadia.utm.registry.ponder;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.ponder.scenes.LaunchContraptionScene;
import com.nadia.utm.utm;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class utmPonderPlugin implements PonderPlugin {
    public static final ResourceLocation A23 = utm.key("2313ag_reqs");

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(utmBlocks.LAUNCH_CONTRAPTION.getId())
                .addStoryBoard("launch_contraption/launch_contraption", LaunchContraptionScene::basic, A23);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> entryHelper = helper.withKeyFunction(RegistryEntry::getId);

        helper.registerTag(A23)
                .addToIndex()
                .item(utmBlocks.LAUNCH_CONTRAPTION.get(), true, false)
                .title("2313AG Components")
                .description("Required components to interact with/enter 2313AG")
                .register();

        entryHelper.addToTag(A23).add(utmBlocks.LAUNCH_CONTRAPTION);
        entryHelper.addToTag(AllCreatePonderTags.CONTRAPTION_ACTOR).add(utmBlocks.LAUNCH_CONTRAPTION);
    }

    @Override
    public @NotNull String getModId() {
        return utm.MODID;
    }

    public void register() {
        PonderIndex.addPlugin(this);
    }
}
