package com.nadia.utm.registry.song;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

public class utmSongs {
    public static final ResourceKey<JukeboxSong> TEARS = create("tears");
    public static final ResourceKey<JukeboxSong> LAVA_CHICKEN = create("lava_chicken");

    private static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("utm", name));
    }
}
