package com.nadia.utm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.NotNull;

public class ColorParticleProvider implements ParticleProvider<ColorParticleOptions> {
    private final SpriteSet sprites;

    public ColorParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(@NotNull ColorParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        return new ColorParticle(level, x, y, z, dx, dy, dz, options, this.sprites);
    }
}