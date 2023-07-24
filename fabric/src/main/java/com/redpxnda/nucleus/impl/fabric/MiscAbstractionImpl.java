package com.redpxnda.nucleus.impl.fabric;

import com.redpxnda.nucleus.fabric.mixin.ParticleEngineAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class MiscAbstractionImpl {
    @Environment(EnvType.CLIENT)
    public static ParticleProvider<?> getProviderFromType(ParticleType<?> type) {
        return ((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getProviders()
                .get(BuiltInRegistries.PARTICLE_TYPE.getId(type));
    }
}
