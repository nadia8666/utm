package com.nadia.utm.particle;

import com.nadia.utm.client.renderer.utmRenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class ColorParticle extends TextureSheetParticle {
    public final SpriteSet sprites;

    public ColorParticle(ClientLevel level, double x, double y, double z, double xSpd, double ySpd, double zSpd, ColorParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);

        this.sprites = sprites;

        this.xd = xSpd;
        this.yd = ySpd;
        this.zd = zSpd;

        this.rCol = options.r();
        this.gCol = options.g();
        this.bCol = options.b();

        this.lifetime = 60;
        this.gravity = 0.0f;
        this.friction = 1.0f;
        this.setSpriteFromAge(sprites);

        this.scale(2.0f);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.removed) {
            this.setSpriteFromAge(this.sprites);
            this.scale(.99f);
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return utmRenderTypes.EMISSIVE_PARTICLE.get();
    }
}
