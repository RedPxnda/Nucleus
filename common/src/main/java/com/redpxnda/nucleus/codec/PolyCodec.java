package com.redpxnda.nucleus.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.EitherCodec;

import java.util.Arrays;
import java.util.List;


/**
 * {@link PolyCodec} is a codec with a similar goal to the {@link EitherCodec}: to allow multiple codecs within one.
 * Unlike {@link EitherCodec}s, {@link PolyCodec}s always decode into their type parameter, whereas {@link EitherCodec}s
 * decode into an {@link Either}. Use {@link PolyCodec} when you hope to allow different formats that all end in the same
 * object, or at least can be mapped into the same object.
 *
 * @param <T> The type this {@link PolyCodec} decodes into.
 */
public class PolyCodec<T> implements Codec<T> {
    private final List<Codec<T>> codecs;

    protected PolyCodec(List<Codec<T>> codecs) {
        this.codecs = codecs;
    }
    @SafeVarargs
    public static <T> PolyCodec<T> of(Codec<T>... codecs) {
        return new PolyCodec<>(Arrays.asList(codecs));
    }

    @Override
    public <E> DataResult<Pair<T, E>> decode(DynamicOps<E> ops, E input) {
        for (Codec<T> codec : codecs) {
            DataResult<Pair<T, E>> result = codec.decode(ops, input);
            if (result.result().isPresent())
                return result.map(r -> r);
        }
        return DataResult.error(() -> "Failed to decode with any of PolyCodec's codecs.");
    }

    @Override
    public <E> DataResult<E> encode(T input, DynamicOps<E> ops, E prefix) {
        for (Codec<T> codec : codecs) {
            DataResult<E> result = codec.encode(input, ops, prefix);
            if (result.result().isPresent())
                return result.map(r -> r);
        }
        return DataResult.error(() -> "Failed to encode with any of PolyCodec's codecs.");
    }
}
