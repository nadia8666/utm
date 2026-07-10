package com.nadia.utm.registry.song;

import com.nadia.utm.utm;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public class utmSongs {
    public static final ResourceKey<JukeboxSong> TEARS = create("tears");
    public static final ResourceKey<JukeboxSong> LAVA_CHICKEN = create("lava_chicken");
    public static final ResourceKey<JukeboxSong> UNDERTALE = create("undertale");
    public static final ResourceKey<JukeboxSong> UNDERTALE2 = create("undertale2");

    private static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, utm.key(name));
    }
}
