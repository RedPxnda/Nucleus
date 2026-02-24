package com.redpxnda.nucleus.impl.forge;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ParticleProviderRegistryImpl {
    public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
        dev.architectury.registry.client.particle.forge.ParticleProviderRegistryImpl.register(type, provider);
    }

    public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProviderRegistry.DeferredParticleProvider<T> provider) {
        dev.architectury.registry.client.particle.forge.ParticleProviderRegistryImpl.register(type, provider);
    }
}
