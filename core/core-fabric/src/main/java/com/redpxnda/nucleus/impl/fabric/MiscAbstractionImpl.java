package com.redpxnda.nucleus.impl.fabric;

import com.redpxnda.nucleus.fabric.mixin.ParticleManagerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

public class MiscAbstractionImpl {
    @Environment(EnvType.CLIENT)
    public static ParticleFactory<?> getProviderFromType(ParticleType<?> type) {
        return ((ParticleManagerAccessor) MinecraftClient.getInstance().particleManager).getFactories()
                .get(Registries.PARTICLE_TYPE.getRawId(type));
    }
}
