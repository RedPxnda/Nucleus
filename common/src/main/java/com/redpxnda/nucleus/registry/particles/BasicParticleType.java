package com.redpxnda.nucleus.registry.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public class BasicParticleType<T extends ParticleOptions> extends ParticleType<T> {
    private final Codec<T> codec;

    public BasicParticleType(boolean bl, Codec<T> codec) {
        super(bl, new CodecDeserializer<>(codec));
        this.codec = codec;
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    public static class CodecDeserializer<T extends ParticleOptions> implements ParticleOptions.Deserializer<T> {
        private final Codec<T> codec;

        public CodecDeserializer(Codec<T> codec) {
            this.codec = codec;
        }

        @Override
        public T fromCommand(ParticleType<T> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            String raw = stringReader.readQuotedString();
            T options = codec.parse(JsonOps.INSTANCE, GsonHelper.parse(raw))
                    .getOrThrow(false, s -> LOGGER.error("Failed to parse options '{}' -> {}", raw, s));
            return options;
        }

        @Override
        public T fromNetwork(ParticleType<T> particleType, FriendlyByteBuf friendlyByteBuf) {
            return ByteBufUtil.readWithCodec(friendlyByteBuf, codec);
        }
    }
}
