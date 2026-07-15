package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.sound.utmSounds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class utmSoundDefsProvider extends SoundDefinitionsProvider {
    public utmSoundDefsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, "utm", existingFileHelper);
    }

    @Override
    public void registerSounds() {
        add(utmSounds.SLIDER_TICK, SoundDefinition.definition().with(
                sound("utm:slider_drag")
        ).subtitle("subtitles.utm.slider_drag"));

        add(utmSounds.MUSIC_DISC_TEARS, SoundDefinition.definition().with(
                sound("utm:records/tears").stream()
        ).subtitle("jukebox_song.utm.tears"));

        add(utmSounds.MUSIC_DISC_LAVA_CHICKEN, SoundDefinition.definition().with(
                sound("utm:records/lava_chicken").stream()
        ).subtitle("jukebox_song.utm.lava_chicken"));

        add(utmSounds.MUSIC_DISC_UNDERTALE, SoundDefinition.definition().with(
                sound("utm:records/undertale").stream()
        ).subtitle("jukebox_song.utm.undertale"));

        add(utmSounds.MUSIC_DISC_UNDERTALE2, SoundDefinition.definition().with(
                sound("utm:records/undertale2").stream()
        ).subtitle("jukebox_song.utm.undertale2"));

        add(utmSounds.SPACE_MUSIC, SoundDefinition.definition().with(
                sound("utm:music/space_1").stream().weight(1),
                sound("utm:music/space_2").stream().weight(1),
                sound("utm:music/space_3").stream().weight(1)
        ));

        add(utmSounds.SR_SWORD, SoundDefinition.definition().with(
                sound("utm:sfx/sr_sword")
        ).subtitle("subtitles.utm.sr_sword"));
        add(utmSounds.SR_HIT, SoundDefinition.definition().with(
                sound("utm:sfx/fl_hit")
        ).subtitle("subtitles.utm.sr_hit"));
        add(utmSounds.SR_THUNDER, SoundDefinition.definition().with(
                sound("utm:sfx/sr_thunder")
        ).subtitle("subtitles.utm.sr_thunder"));
        add(utmSounds.SR_BOXING, SoundDefinition.definition().with(
                sound("utm:sfx/sr_boxing")
        ).subtitle("subtitles.utm.sr_boxing"));
        add(utmSounds.PKFRS, SoundDefinition.definition().with(
                sound("utm:sfx/pkfirebegin")
        ).subtitle("subtitles.utm.pkfrs"));
        add(utmSounds.PKFRS2, SoundDefinition.definition().with(
                sound("utm:sfx/pkfrs2")
        ).subtitle("subtitles.utm.pkfrs2"));
    }
}
