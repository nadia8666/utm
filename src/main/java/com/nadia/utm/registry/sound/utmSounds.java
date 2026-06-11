package com.nadia.utm.registry.sound;

import com.nadia.utm.utm;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class utmSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "utm");

    public static final DeferredHolder<SoundEvent, SoundEvent> SLIDER_TICK = SOUNDS.register("slider_drag",
            () -> SoundEvent.createVariableRangeEvent(utm.key("slider_drag")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_TEARS = SOUNDS.register("music_disc_tears",
            () -> SoundEvent.createVariableRangeEvent(utm.key("music_disc_tears")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_LAVA_CHICKEN = SOUNDS.register("music_disc_lava_chicken",
            () -> SoundEvent.createVariableRangeEvent(utm.key("music_disc_lava_chicken")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_UNDERTALE = SOUNDS.register("music_disc_undertale",
            () -> SoundEvent.createVariableRangeEvent(utm.key("music_disc_undertale")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPACE_MUSIC = SOUNDS.register("space_music",
            () -> SoundEvent.createVariableRangeEvent(utm.key("space_music"))); // yay ;D

    public static final DeferredHolder<SoundEvent, SoundEvent> SR_SWORD = SOUNDS.register("sr_sword",
            () -> SoundEvent.createVariableRangeEvent(utm.key("sr_sword")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SR_THUNDER = SOUNDS.register("sr_thunder",
            () -> SoundEvent.createVariableRangeEvent(utm.key("sr_thunder")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SR_HIT = SOUNDS.register("sr_hit",
            () -> SoundEvent.createVariableRangeEvent(utm.key("sr_hit"))); // this hsound has a little really high pitchedn oise you mgith want to kill (DONE killed
    public static final DeferredHolder<SoundEvent, SoundEvent> SR_BOXING = SOUNDS.register("sr_boxing",
            () -> SoundEvent.createVariableRangeEvent(utm.key("sr_boxing")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PKFRS = SOUNDS.register("pkfrs",
            () -> SoundEvent.createVariableRangeEvent(utm.key("pkfrs")));

}



