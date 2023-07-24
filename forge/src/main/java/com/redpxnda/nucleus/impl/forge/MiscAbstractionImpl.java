package com.redpxnda.nucleus.impl.forge;

import com.redpxnda.nucleus.forge.mixin.ParticleEngineAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class MiscAbstractionImpl {
    @OnlyIn(Dist.CLIENT)
    public static ParticleProvider<?> getProviderFromType(ParticleType<?> type) {
        return ((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getProviders()
                .get(ForgeRegistries.PARTICLE_TYPES.getKey(type));
    }
}
