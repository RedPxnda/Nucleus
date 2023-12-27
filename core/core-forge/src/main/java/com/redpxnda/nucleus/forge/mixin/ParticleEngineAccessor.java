package com.redpxnda.nucleus.forge.mixin;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ParticleManager.class)
public interface ParticleEngineAccessor {
    @Accessor("f_107293_")
    Map<Identifier, ParticleFactory<?>> getProviders();
}
