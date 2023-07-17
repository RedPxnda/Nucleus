package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

@AutoCodec.Settings(optionalByDefault = true)
public class EmitterParticleOptions extends DynamicParticleOptions {
    public @AutoCodec.Mandatory ParticleOptions emit;
    public Supplier<Double> frequency = () -> 1d, count = () -> 1d, speed = () -> 0d;
    public int startAfter = 0;

    public EmitterParticleOptions() {}

    public static final Codec<EmitterParticleOptions> codec = AutoCodec.of(EmitterParticleOptions.class).codec();

    @Override
    public Codec<? extends DynamicParticleOptions> codec() {
        return codec;
    }

    @Override
    public ParticleType<?> getType() {
        return NucleusRegistries.emittingParticle.get();
    }
}
