package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@AutoCodec.Settings(optionalByDefault = true)
public class BlockChunkParticleOptions extends DynamicParticleOptions {
    public Block block = Blocks.STONE;

    public static final Codec<BlockChunkParticleOptions> codec = AutoCodec.of(BlockChunkParticleOptions.class).codec();

    @Override
    public Codec<BlockChunkParticleOptions> codec() {
        return codec;
    }

    @Override
    public ParticleType<?> getType() {
        return NucleusRegistries.blockChunkParticle.get();
    }
}
