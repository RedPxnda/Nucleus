package com.redpxnda.nucleus.codec.misc;

import com.mojang.serialization.*;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import org.slf4j.Logger;

import java.util.stream.Stream;

public class OptionalMapCodec<A> extends MapCodec<A> implements AutoCodec.NullabilityHandler {
    private static final Logger LOGGER = Nucleus.getLogger();
    public final String name;
    public final Codec<A> codec;
    public final CodecBehavior.Optional optional;

    public OptionalMapCodec(String name, Codec<A> codec, CodecBehavior.Optional optional) {
        this.name = name;
        this.codec = codec;
        this.optional = optional;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.of(ops.createString(name));
    }

    @Override
    public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
        T element = input.get(name);
        if (element == null) return DataResult.success(null);
        return codec.parse(ops, element);
    }

    @Override
    public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        if (input == null && optional.encodeToNull())
            prefix.add(ops.createString(name), ops.empty());
        else if (input != null)
            prefix.add(ops.createString(name), codec.encodeStart(ops, input).getOrThrow(false, s -> LOGGER.error("Failed to encode optional object '{}'! -> {}", input, s)));
        return prefix;
    }

    @Override
    public <T> boolean shouldSetToNull(DynamicOps<T> ops, MapLike<T> map) {
        return false;
    }
}
