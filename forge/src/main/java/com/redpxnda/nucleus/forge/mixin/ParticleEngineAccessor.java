package com.redpxnda.nucleus.forge.mixin;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
    @Accessor("f_107293_")
    Map<ResourceLocation, ParticleProvider<?>> getProviders();
}
