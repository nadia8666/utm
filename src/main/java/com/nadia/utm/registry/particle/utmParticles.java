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

@EventBusSubscriber(modid = "utm", value = Dist.CLIENT)
public class utmParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "utm");

    private static final Map<String, DeferredHolder<ParticleType<?>, ColorParticleType>> COLOR_PARTICLES = new HashMap<>();
    private static DeferredHolder<ParticleType<?>, ColorParticleType> registerColorParticle(String name) {
        var holder = PARTICLE_TYPES.register(name, ColorParticleType::new);
        COLOR_PARTICLES.put(name, holder);

        return holder;
    }

    public static final class Trails {
        public static final DeferredHolder<ParticleType<?>, ColorParticleType> VEIN =
                PARTICLE_TYPES.register("vein_trail", ColorParticleType::new);

        public static final DeferredHolder<ParticleType<?>, ColorParticleType> OUTWARD =
                PARTICLE_TYPES.register("outward_trail", ColorParticleType::new);

        public static final DeferredHolder<ParticleType<?>, ColorParticleType> LESSER =
                PARTICLE_TYPES.register("lesser_trail", ColorParticleType::new);

        public static final DeferredHolder<ParticleType<?>, ColorParticleType> ECOLOGIST =
                PARTICLE_TYPES.register("ecologist_trail", ColorParticleType::new);

        public static final DeferredHolder<ParticleType<?>, ColorParticleType> HEARTSTWINGS =
                PARTICLE_TYPES.register("heartstwings_trail", ColorParticleType::new);

        public static final DeferredHolder<ParticleType<?>, ColorParticleType> ROADRUNNER =
                PARTICLE_TYPES.register("roadrunner_trail", ColorParticleType::new);

        public static final DeferredHolder<ParticleType<?>, ColorParticleType> SPADES =
                PARTICLE_TYPES.register("spades_trail", ColorParticleType::new);
    }

    public static DeferredHolder<ParticleType<?>, ColorParticleType> getFromString(String name) {
        return COLOR_PARTICLES.get(name + "_trail");
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        COLOR_PARTICLES.forEach((ignored, particle) -> event.registerSpriteSet(particle.get(), ColorParticleProvider::new));
    }
}
