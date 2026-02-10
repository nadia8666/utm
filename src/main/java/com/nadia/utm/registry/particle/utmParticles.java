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

@EventBusSubscriber(modid = "utm", value = Dist.CLIENT)
public class utmParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "utm");

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> VEIN_TRAIL =
            PARTICLE_TYPES.register("vein_trail", ColorParticleType::new);

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> OUTWARD_TRAIL =
            PARTICLE_TYPES.register("outward_trail", ColorParticleType::new);

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> LESSER_TRAIL =
            PARTICLE_TYPES.register("lesser_trail", ColorParticleType::new);

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(utmParticles.VEIN_TRAIL.get(), ColorParticleProvider::new);
        event.registerSpriteSet(utmParticles.OUTWARD_TRAIL.get(), ColorParticleProvider::new);
        event.registerSpriteSet(utmParticles.LESSER_TRAIL.get(), ColorParticleProvider::new);
    }
}
