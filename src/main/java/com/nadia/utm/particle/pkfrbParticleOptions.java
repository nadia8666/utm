package com.nadia.utm.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record pkfrbParticleOptions(ParticleType<pkfrbParticleOptions> type, int r) implements ParticleOptions {
    public static MapCodec<pkfrbParticleOptions> codec(ParticleType<pkfrbParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("r").forGetter(pkfrbParticleOptions::r)
        ).apply(instance, (r) -> new pkfrbParticleOptions(type, r)));
    }
    public static StreamCodec<RegistryFriendlyByteBuf, pkfrbParticleOptions> streamCodec(ParticleType<pkfrbParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.INT, pkfrbParticleOptions::r,
                (r) -> new pkfrbParticleOptions(type, r)
        );
    }

    @Override
    public @NotNull ParticleType<pkfrbParticleOptions> getType() {
        return this.type;
    }
}