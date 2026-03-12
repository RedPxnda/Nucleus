package com.redpxnda.nucleus.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Revival of {@link dev.architectury.registry.client.particle.ParticleProviderRegistry}
 */
@Environment(EnvType.CLIENT)
public class ParticleProviderRegistry {
    @ExpectPlatform
    public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends ParticleOptions> void register(ParticleType<T> type, dev.architectury.registry.client.particle.ParticleProviderRegistry.DeferredParticleProvider<T> provider) {
        throw new AssertionError();
    }
}
