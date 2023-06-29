package com.redpxnda.nucleus.registry.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.datapack.codec.DoubleSupplier;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ByteBufUtil;
import com.redpxnda.nucleus.util.Defaulted;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

@AutoCodec.Settings(optionalByDefault = true)
public class EmittingParticleOptions implements ParticleOptions {
    int lifetime = 100; @AutoCodec.Mandatory ParticleOptions emit; Supplier<Double> frequency = () -> 1d; Supplier<Double> count = () -> 1d;
    Supplier<Double> speed = () -> 0d; float gravity = 0f; float friction = 0.98f;
    boolean physics = true; int startAfter = 0;

    public EmittingParticleOptions() {}
    public EmittingParticleOptions(int lifetime, ParticleOptions emit, Supplier<Double> frequency, Supplier<Double> count, Supplier<Double> speed, float gravity, float friction, boolean physics, int startAfter) {
        this.lifetime = lifetime;
        this.emit = emit;
        this.frequency = frequency;
        this.count = count;
        this.speed = speed;
        this.gravity = gravity;
        this.friction = friction;
        this.physics = physics;
        this.startAfter = startAfter;
    }

    public static final Codec<EmittingParticleOptions> codec = AutoCodec.of(EmittingParticleOptions.class).codec();
    /*RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("lifetime").forGetter(i -> i.lifetime),
            ParticleTypes.CODEC.fieldOf("emit").forGetter(i -> i.emit),
            DoubleSupplier.SUP_CODEC.optionalFieldOf("frequency", () -> 1d).forGetter(i -> i.frequency),
            DoubleSupplier.SUP_CODEC.optionalFieldOf("count", () -> 1d).forGetter(i -> i.count),
            DoubleSupplier.SUP_CODEC.optionalFieldOf("speed", () -> 0d).forGetter(i -> i.speed),
            Codec.FLOAT.optionalFieldOf("gravity", 0f).forGetter(i -> i.gravity),
            Codec.FLOAT.optionalFieldOf("friction", 0.98f).forGetter(i -> i.friction),
            Codec.BOOL.optionalFieldOf("physics", true).forGetter(i -> i.physics),
            Codec.INT.optionalFieldOf("startAfter", 0).forGetter(i -> i.startAfter)
    ).apply(inst, EmittingParticleOptions::new));*/

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
