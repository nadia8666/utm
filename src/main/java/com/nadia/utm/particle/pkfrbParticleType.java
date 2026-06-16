package com.nadia.utm.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class pkfrbParticleType extends ParticleType<pkfrbParticleOptions> {
    public pkfrbParticleType() {
        super(false);
    }

    @Override
    public @NotNull MapCodec<pkfrbParticleOptions> codec() {
        return pkfrbParticleOptions.codec(this);
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, pkfrbParticleOptions> streamCodec() {
        return pkfrbParticleOptions.streamCodec(this);
    }
}