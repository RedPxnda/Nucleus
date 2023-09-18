package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import net.minecraft.block.Block;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;

import static com.redpxnda.nucleus.Nucleus.loc;

@AutoCodec.Settings(optionalByDefault = true)
public class ChunkParticleOptions extends DynamicParticleOptions {
    public Block block = null;
    public Identifier texture = loc("item/blank");

    public static final Codec<ChunkParticleOptions> codec = AutoCodec.of(ChunkParticleOptions.class).codec();

    @Override
    public Codec<ChunkParticleOptions> codec() {
        return codec;
    }

    @Override
    public ParticleType<?> getType() {
        return NucleusRegistries.blockChunkParticle.get();
    }
}
