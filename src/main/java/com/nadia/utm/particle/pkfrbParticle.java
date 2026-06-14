package com.nadia.utm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.NotNull;

public class pkfrbParticle extends TextureSheetParticle {
    public final SpriteSet sprites;

    public pkfrbParticle(ClientLevel level, double x, double y, double z, double xSpd, double ySpd, double zSpd, ParticleOptions ignored, SpriteSet sprites) {
        super(level, x, y, z);

        this.sprites = sprites;

        this.xd = xSpd;
        this.yd = ySpd;
        this.zd = zSpd;

        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;


        //https://github.com/nadia8666/bee-mod/blob/master/src/main/java/com/ihatebees/particle/custom/StarSweepParticle.java
        this.lifetime = 4;
        this.gravity = 0.0f;
        this.friction = 2.0f - (float)0*0.5f; // the thing i copied from has D but this does not have D so idk what D iss but
        //that thig above is meant to b e scale lol Friction is roblox equivalent of drag
        this.setSpriteFromAge(sprites);

        this.scale(4.0f);
    }

    public void tick() {
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
