package com.nadia.utm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class pkfrbProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public pkfrbProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }
    // is there anything unique to you that means i cant just use you for every simple particle i make?
    @Override
    public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        return new pkfrbParticle(level, x, y, z, dx, dy, dz, this.sprites);
    }
}