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
    }
}
