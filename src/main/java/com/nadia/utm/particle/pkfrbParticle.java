package com.nadia.utm.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class pkfrbParticle extends TextureSheetParticle {
    public final SpriteSet sprites;
    public Entity arid;

    public pkfrbParticle(ClientLevel level, double x, double y, double z, double xSpd, double ySpd, double zSpd, pkfrbParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);

        this.arid = level.getEntity(options.r());
        this.x = arid.getX();
        this.y = arid.getY();
        this.z = arid.getZ();
        this.xo = arid.getX();
        this.yo = arid.getY();
        this.zo = arid.getZ();

        this.sprites = sprites;
        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;


        this.lifetime = 10;
        this.gravity = 0.0f;
        this.friction = 0f;
        this.setSpriteFromAge(sprites);

        this.scale(8.0f); //verifiy that scale actually works lol it might just not
    }

    public int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 pos = this.arid.getPosition(partialTicks);
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;
        if (! (this.age+partialTicks>=this.lifetime))
             this.setSpriteFromAge2(this.sprites,partialTicks);

        super.render(buffer, renderInfo, partialTicks);
    }

    public void tick() {

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    public int interpolation(float progress) {
        // lifetime/frames
        // how much of the lifetime you need to cut per frame
        float alpha = Math.clamp(progress/this.lifetime,0,1);
        //now input ticks and pt to see how many ticksperframe you are


        return (int) Math.round(alpha*13f);
    }

    public void setSpriteFromAge2(SpriteSet sprite, float pt) {
        if (!this.removed) {
            this.setSprite(sprite.get(this.interpolation(this.age+pt), 13));
        }

    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public int getLifetime() {
        return this.lifetime; // 8 yaya
    }
}
