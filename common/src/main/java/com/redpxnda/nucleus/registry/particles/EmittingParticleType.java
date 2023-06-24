package com.redpxnda.nucleus.registry.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.codec.DoubleSupplier;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

import java.util.function.Supplier;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public class EmittingParticleType extends ParticleType<EmittingParticleType.Options> {
    public static final Codec<Options> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("lifetime").forGetter(i -> i.lifetime),
            ParticleTypes.CODEC.fieldOf("emit").forGetter(i -> i.emit),
            DoubleSupplier.SUP_CODEC.fieldOf("frequency").forGetter(i -> i.freq),
            DoubleSupplier.SUP_CODEC.fieldOf("count").forGetter(i -> i.count),
            DoubleSupplier.SUP_CODEC.fieldOf("speed").forGetter(i -> i.speed)
    ).apply(inst, Options::new));

    public EmittingParticleType(boolean alwaysShow) {
        super(alwaysShow, Options.DESERIALIZER);
    }

    @Override
    public Codec<Options> codec() {
        return CODEC;
    }

    public record Options(int lifetime, ParticleOptions emit, Supplier<Double> freq, Supplier<Double> count, Supplier<Double> speed) implements ParticleOptions {
        @Override
        public ParticleType<?> getType() {
            return NucleusRegistries.emittingParticle.get();
        }

        @Override
        public void writeToNetwork(FriendlyByteBuf buf) {
            ByteBufUtil.writeWithCodec(buf, this, CODEC);
        }

        @Override
        public String writeToString() {
            return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()).toString();
        }

        public static final ParticleOptions.Deserializer<Options> DESERIALIZER = new Deserializer<>() {
            @Override
            public Options fromCommand(ParticleType<Options> particleType, StringReader stringReader) throws CommandSyntaxException {
                stringReader.expect(' ');
                int l = stringReader.readInt();
                stringReader.expect(' ');
                String raw = stringReader.readQuotedString();
                ParticleOptions particle = ParticleTypes.CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(raw))
                        .getOrThrow(false, s -> LOGGER.error("Failed to parse particle '{}' -> {}", raw, s));
                stringReader.expect(' ');
                double freq = stringReader.readDouble();
                stringReader.expect(' ');
                double count = stringReader.readDouble();
                stringReader.expect(' ');
                double speed = stringReader.readDouble();

                return new Options(l, particle, () -> freq, () -> count, () -> speed);
            }

            @Override
            public Options fromNetwork(ParticleType<Options> particleType, FriendlyByteBuf buf) {
                return ByteBufUtil.readWithCodec(buf, CODEC);
            }
        };
    }
}
