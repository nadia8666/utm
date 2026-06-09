package com.nadia.utm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;

public class RedSweepParticle extends TextureSheetParticle {
    public final SpriteSet sprites;

    public RedSweepParticle(ClientLevel level, double x, double y, double z, double xSpd, double ySpd, double zSpd, ParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);

        this.sprites = sprites;

        this.xd = xSpd;
        this.yd = ySpd;
        this.zd = zSpd;

        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;

        this.lifetime = 8;
        this.gravity = 0.0f;
        this.friction = 1.0f;
        this.setSpriteFromAge(sprites);

        this.scale(2.0f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLifetime() {
        return 8; // 8 yaya
    }
}
