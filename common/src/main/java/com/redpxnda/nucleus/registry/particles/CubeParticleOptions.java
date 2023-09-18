package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import net.minecraft.particle.ParticleType;

@AutoCodec.Settings(optionalByDefault = true)
public class CubeParticleOptions extends DynamicParticleOptions {
    public boolean invert = false;
    public float xSize = 1, ySize = 1, zSize = 1;

    public static final Codec<CubeParticleOptions> codec = AutoCodec.of(CubeParticleOptions.class).codec();

    @Override
    public Codec<CubeParticleOptions> codec() {
        return codec;
    }

    @Override
    public ParticleType<?> getType() {
        return NucleusRegistries.cubeParticle.get();
    }
}
