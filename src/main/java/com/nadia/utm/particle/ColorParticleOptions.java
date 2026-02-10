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

public record ColorParticleOptions(ParticleType<ColorParticleOptions> type, float r, float g, float b) implements ParticleOptions {
    public static MapCodec<ColorParticleOptions> codec(ParticleType<ColorParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("r").forGetter(ColorParticleOptions::r),
                Codec.FLOAT.fieldOf("g").forGetter(ColorParticleOptions::g),
                Codec.FLOAT.fieldOf("b").forGetter(ColorParticleOptions::b)
        ).apply(instance, (r, g, b) -> new ColorParticleOptions(type, r, g, b)));
    }
    public static StreamCodec<RegistryFriendlyByteBuf, ColorParticleOptions> streamCodec(ParticleType<ColorParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.FLOAT, ColorParticleOptions::r,
                ByteBufCodecs.FLOAT, ColorParticleOptions::g,
                ByteBufCodecs.FLOAT, ColorParticleOptions::b,
                (r, g, b) -> new ColorParticleOptions(type, r, g, b)
        );
    }

    @Override
    public @NotNull ParticleType<ColorParticleOptions> getType() {
        return this.type;
    }
}