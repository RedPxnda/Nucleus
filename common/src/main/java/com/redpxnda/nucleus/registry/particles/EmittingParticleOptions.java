package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.codec.DoubleSupplier;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public record EmittingParticleOptions(int lifetime, ParticleOptions emit, Supplier<Double> freq, Supplier<Double> count,
                                      Supplier<Double> speed, float gravity, float friction,
                                      boolean physics, int start) implements ParticleOptions {
    public static final Codec<EmittingParticleOptions> codec = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("lifetime").forGetter(i -> i.lifetime),
            ParticleTypes.CODEC.fieldOf("emit").forGetter(i -> i.emit),
            DoubleSupplier.SUP_CODEC.optionalFieldOf("frequency", () -> 1d).forGetter(i -> i.freq),
            DoubleSupplier.SUP_CODEC.optionalFieldOf("count", () -> 1d).forGetter(i -> i.count),
            DoubleSupplier.SUP_CODEC.optionalFieldOf("speed", () -> 0d).forGetter(i -> i.speed),
            Codec.FLOAT.optionalFieldOf("gravity", 0f).forGetter(i -> i.gravity),
            Codec.FLOAT.optionalFieldOf("friction", 0.98f).forGetter(i -> i.friction),
            Codec.BOOL.optionalFieldOf("physics", true).forGetter(i -> i.physics),
            Codec.INT.optionalFieldOf("startAfter", 0).forGetter(i -> i.start)
    ).apply(inst, EmittingParticleOptions::new));

    @Override
    public ParticleType<?> getType() {
        return NucleusRegistries.emittingParticle.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        ByteBufUtil.writeWithCodec(buf, this, codec);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()).toString();
    }
}
