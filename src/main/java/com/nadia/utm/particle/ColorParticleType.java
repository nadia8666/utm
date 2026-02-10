package com.nadia.utm.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class ColorParticleType extends ParticleType<ColorParticleOptions> {
    public ColorParticleType() {
        super(false);
    }

    @Override
    public @NotNull MapCodec<ColorParticleOptions> codec() {
        return ColorParticleOptions.codec(this);
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleOptions> streamCodec() {
        return ColorParticleOptions.streamCodec(this);
    }
}