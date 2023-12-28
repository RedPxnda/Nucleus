package com.redpxnda.nucleus.codec.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.BiFunction;

/**
 * {@link IntermediateCodec} with a more customizable output object via the 4th type parameter.
 */
public class CustomIntermediateCodec<S, I, O, M extends IntermediateCodec.Median<S, I, O>> implements Codec<M> {
    private final Codec<S> start;
    private final BiFunction<S, I, O> converter;
    private final BiFunction<S, BiFunction<S, I, O>, M> medianCreator;

    public CustomIntermediateCodec(Codec<S> start, BiFunction<S, I, O> converter, BiFunction<S, BiFunction<S, I, O>, M> medianCreator) {
        this.start = start;
        this.converter = converter;
        this.medianCreator = medianCreator;
    }

    @Override
    public <T> DataResult<Pair<M, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<S, T>> s = start.decode(ops, input);
        if (s.result().isPresent())
            return DataResult.success(Pair.of(createMedian(s.result().get().getFirst(), converter), input));
        return DataResult.error(() -> "Failed to decode starting codec: " + (s.error().isPresent() ? s.error().get().message() : "<unavailable>"));
    }

    @Override
    public <T> DataResult<T> encode(M input, DynamicOps<T> ops, T prefix) {
        return start.encode(input.start, ops, prefix);
    }

    public M createMedian(S start, BiFunction<S, I, O> converter) {
        return medianCreator.apply(start, converter);
    }
}
