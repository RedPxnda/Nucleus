package com.redpxnda.nucleus.datapack.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.BiFunction;

/**
 * {@link IntermediateCodec} is a Codec representing a value that can be converted to another, but only when an input is provided.
 * As opposed to xmapping a codec, {@link IntermediateCodec}s should be used when you would need extra data in order to map it.
 * For example, say I had some sort of expression that can be representable in json with a string. "17*x+45/2". The desired goal is
 * to evaluate this expression when inputting some varying value for x that is only generated at runtime. For this circumstance,
 * I would use an {@link IntermediateCodec}. Specifically: {@link IntermediateCodec}< String, Number, Number >.
 * When decoding with this codec, you will be returned a {@link Median}. Use the "evaluate" method to then get a proper output.
 *
 * @param <S> The type of starting value that will be converted into some output when provided with an input.
 * @param <I> The type of input that will be provided to produce the output.
 * @param <O> The type of the output.
 */
public class IntermediateCodec<S, I, O> implements Codec<IntermediateCodec.Median<S, I, O>> {
    private final Codec<S> start;
    private final BiFunction<S, I, O> converter;

    private IntermediateCodec(Codec<S> start, BiFunction<S, I, O> converter) {
        this.start = start;
        this.converter = converter;
    }
    public static <S, I, O> IntermediateCodec<S, I, O> of(Codec<S> start, BiFunction<S, I, O> converter) {
        return new IntermediateCodec<>(start, converter);
    }

    @Override
    public <T> DataResult<Pair<Median<S, I, O>, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<S, T>> s = start.decode(ops, input);
        if (s.result().isPresent())
            return DataResult.success(Pair.of(new Median<>(s.result().get().getFirst(), converter), input));
        return DataResult.error(() -> "Failed to decode starting codec: " + (s.error().isPresent() ? s.error().get().message() : "<unavailable>"));
    }

    @Override
    public <T> DataResult<T> encode(Median<S, I, O> input, DynamicOps<T> ops, T prefix) {
        return start.encode(input.start, ops, prefix);
    }


    public static class Median<S, I, O> {
        protected final S start;
        protected final BiFunction<S, I, O> converter;

        public Median(S start, BiFunction<S, I, O> converter) {
            this.start = start;
            this.converter = converter;
        }

        public O evaluate(I input) {
            return converter.apply(start, input);
        }
    }
}
