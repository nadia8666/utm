package com.nadia.utm.particle;

import com.nadia.utm.utm;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.NotNull;

public class GenericParticleProvider<O extends ParticleOptions> implements ParticleProvider<O> {
    private final SpriteSet sprites;
    private final ParticleFactory<O> factory;

    @FunctionalInterface
    public interface ParticleFactory<O extends ParticleOptions> {
        Particle create(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, O options, SpriteSet sprites);
    }

    public GenericParticleProvider(SpriteSet sprites, ParticleFactory<O> particle) {
        this.sprites = sprites;
        this.factory = particle;
    }

    @Override
    public Particle createParticle(@NotNull O options, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        try {
            return this.factory.create(level, x, y, z, dx, dy, dz, options, this.sprites);
        } catch (Exception e) {
            utm.LOGGER.error("[UTM] Failed to create particle", e);
            return null;
        }
    }
}
