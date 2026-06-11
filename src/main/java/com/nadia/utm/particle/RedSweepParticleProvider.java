package com.nadia.utm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class RedSweepParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public RedSweepParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        return new RedSweepParticle(level, x, y, z, dx, dy, dz, this.sprites);
    }
}