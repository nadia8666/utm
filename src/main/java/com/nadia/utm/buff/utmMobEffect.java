package com.nadia.utm.buff;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class utmMobEffect extends MobEffect {
    public utmMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public utmMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }
    // this class is e*stein files
}
