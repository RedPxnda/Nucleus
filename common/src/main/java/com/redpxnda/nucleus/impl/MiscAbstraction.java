package com.redpxnda.nucleus.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;

public class MiscAbstraction {
    @ExpectPlatform
    @Environment(EnvType.CLIENT)
    public static ParticleProvider<?> getProviderFromType(ParticleType<?> type) {
        throw new AssertionError();
    }
}
