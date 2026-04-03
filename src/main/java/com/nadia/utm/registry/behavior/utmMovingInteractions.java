package com.nadia.utm.registry.behavior;

import com.nadia.utm.behavior.LaunchContraptionInteraction;
import com.nadia.utm.registry.block.utmBlocks;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;

public class utmMovingInteractions {
    public static void register() {
        MovingInteractionBehaviour.REGISTRY.register(utmBlocks.LAUNCH_CONTRAPTION.get(), new LaunchContraptionInteraction());
    }
}
