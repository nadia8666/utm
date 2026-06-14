package com.nadia.utm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class pkfrbParticle extends TextureSheetParticle {
    public final SpriteSet sprites;
    public Entity arid;

    public pkfrbParticle(ClientLevel level, double x, double y, double z, int xSpd, double ySpd, double zSpd, ParticleOptions ignored, SpriteSet sprites) {
        super(level, x, y, z);


        this.arid = level.getEntity(xSpd);;
        this.x = arid.getX();
        this.y = arid.getY();
        this.z = arid.getZ();


        this.sprites = sprites;
        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;


        this.lifetime = 14;
        this.gravity = 0.0f;
        this.friction = 0f;
        this.setSpriteFromAge(sprites);

        this.scale(8.0f); //verifiy that scale actually works lol it might just not
    }

    public void tick() {
        this.x = arid.getX();
        this.y = arid.getY();
        this.z = arid.getZ();
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }
    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLifetime() {
        return 14; // 8 yaya
    }
}
