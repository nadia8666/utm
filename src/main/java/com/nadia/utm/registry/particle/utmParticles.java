package com.nadia.utm.registry.particle;

import com.nadia.utm.particle.ColorParticleProvider;
import com.nadia.utm.particle.ColorParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = "utm", value = Dist.CLIENT)
public class utmParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "utm");

    private static final Map<String, DeferredHolder<ParticleType<?>, ColorParticleType>> COLOR_PARTICLES = new HashMap<>();

    private static DeferredHolder<ParticleType<?>, ColorParticleType> registerColorParticle(String name) {
        var holder = PARTICLE_TYPES.register(name + "_trail", ColorParticleType::new);
        COLOR_PARTICLES.put(name + "_trail", holder);

        return holder;
    }

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

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        COLOR_PARTICLES.forEach((name, particle) -> event.registerSpriteSet(particle.get(), ColorParticleProvider::new));
    }
}
