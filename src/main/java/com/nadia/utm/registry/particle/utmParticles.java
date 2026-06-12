package com.nadia.utm.registry.particle;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import com.nadia.utm.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@ForceLoad(dist = Dist.CLIENT)
public class utmParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "utm");

    private static final Map<String, DeferredHolder<ParticleType<?>, ColorParticleType>> COLOR_PARTICLES = new HashMap<>();

    private static DeferredHolder<ParticleType<?>, ColorParticleType> registerColorParticle(String name) {
        var holder = PARTICLE_TYPES.register(name + "_trail", ColorParticleType::new);
        COLOR_PARTICLES.put(name + "_trail", holder);

        return holder;
    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_SWEEP = PARTICLE_TYPES.register("rslash", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PKFIREBEGIN = PARTICLE_TYPES.register("pkfirebegin", () -> new SimpleParticleType(true));


    public static final DeferredHolder<ParticleType<?>, ColorParticleType> VEIN =
            registerColorParticle("vein");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> OUTWARD =
            registerColorParticle("outward");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> LESSER =
            registerColorParticle("lesser");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> ECOLOGIST =
            registerColorParticle("ecologist");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> HEARTSTWINGS =
            registerColorParticle("heartstwings");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> ROADRUNNER =
            registerColorParticle("roadrunner");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> SPADES =
            registerColorParticle("spades");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> NEP =
            registerColorParticle("nep");

    public static DeferredHolder<ParticleType<?>, ColorParticleType> getFromString(String name) {
        return COLOR_PARTICLES.get(name + "_trail");
    }

    static {
        utmEventHost.register(RegisterParticleProvidersEvent.class, event -> {
            COLOR_PARTICLES.forEach((name, particle) -> event.registerSpriteSet(particle.get(), sprites -> new GenericParticleProvider<>(sprites, ColorParticle::new)));
            event.registerSpriteSet(RED_SWEEP.get(), sprites -> new GenericParticleProvider<>(sprites, RedSweepParticle::new));
            event.registerSpriteSet(PKFIREBEGIN.get(), sprites -> new GenericParticleProvider<>(sprites, pkfrbParticle::new)); // im lazy
        });
    }
}
