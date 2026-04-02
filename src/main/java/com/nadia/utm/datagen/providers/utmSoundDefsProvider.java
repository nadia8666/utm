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

        add(utmSounds.AG_MUSIC, SoundDefinition.definition().with(
                sound("utm:music/2313ag_1").stream().weight(1),
                sound("utm:music/2313ag_2").stream().weight(1),
                sound("utm:music/2313ag_3").stream().weight(1)
        ));
    }
}
