package com.nadia.utm.registry.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class utmSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "utm");

    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDER_TICK = SOUNDS.register("slider_drag",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("utm", "slider_drag")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_TEARS = SOUNDS.register("music_disc_tears",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("utm", "music_disc_tears")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_LAVA_CHICKEN = SOUNDS.register("music_disc_lava_chicken",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("utm", "music_disc_lava_chicken")));
}
