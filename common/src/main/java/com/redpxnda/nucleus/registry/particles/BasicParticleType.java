package com.redpxnda.nucleus.registry.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.util.ByteBufUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.JsonHelper;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

public class BasicParticleType<T extends ParticleEffect> extends ParticleType<T> {
    private final Codec<T> codec;

    public BasicParticleType(boolean bl, Codec<T> codec) {
        super(bl, new CodecDeserializer<>(codec));
        this.codec = codec;
    }

    @Override
    public Codec<T> getCodec() {
        return codec;
    }

    public static class CodecDeserializer<T extends ParticleEffect> implements ParticleEffect.Factory<T> {
        private final Codec<T> codec;

        public CodecDeserializer(Codec<T> codec) {
            this.codec = codec;
        }

        @Override
        public T read(ParticleType<T> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            String raw = stringReader.readQuotedString();
            T options = codec.parse(JsonOps.INSTANCE, JsonHelper.deserialize(raw))
                    .getOrThrow(false, s -> LOGGER.error("Failed to parse options '{}' -> {}", raw, s));
            return options;
        }

        @Override
        public T read(ParticleType<T> particleType, PacketByteBuf friendlyByteBuf) {
            return ByteBufUtil.readWithCodec(friendlyByteBuf, codec);
        }
    }
}
