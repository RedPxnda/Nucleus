package com.redpxnda.nucleus.impl.forge;

import com.redpxnda.nucleus.forge.mixin.ParticleEngineAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class MiscAbstractionImpl {
    @OnlyIn(Dist.CLIENT)
    public static ParticleFactory<?> getProviderFromType(ParticleType<?> type) {
        return ((ParticleEngineAccessor) MinecraftClient.getInstance().particleManager).getProviders()
                .get(ForgeRegistries.PARTICLE_TYPES.getKey(type));
    }
}
