package com.redpxnda.nucleus.datapack.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.List;

/**
 * {@link PolyMedialCodec}s are essentially a mix between {@link PolyCodec} and {@link IntermediateCodec}.
 * See their respective documentations to get a better idea of how to use this one.
 */
public class PolyMedialCodec<S, I, O> implements Codec<IntermediateCodec.Median<S, I, O>> {
    private final List<IntermediateCodec<S, I, O>> codecs;

    public PolyMedialCodec(List<IntermediateCodec<S, I, O>> codecs) {
        this.codecs = codecs;
    }

    @Override
    public <T> DataResult<Pair<IntermediateCodec.Median<S, I, O>, T>> decode(DynamicOps<T> ops, T input) {
        for (IntermediateCodec<S, I, O> codec : codecs) {
            DataResult<Pair<IntermediateCodec.Median<S, I, O>, T>> result = codec.decode(ops, input);
            if (result.result().isPresent())
                return result.map(r -> r);
        }
        return DataResult.error("Failed to decode with any of PolyMedialCodec's IntermediateCodecs.");
    }

    @Override
    public <T> DataResult<T> encode(IntermediateCodec.Median<S, I, O> input, DynamicOps<T> ops, T prefix) {
        for (IntermediateCodec<S, I, O> codec : codecs) {
            DataResult<T> result = codec.encode(input, ops, prefix);
            if (result.result().isPresent())
                return result.map(r -> r);
        }
        return DataResult.error("Failed to encode with any of PolyMedialCodec's IntermediateCodecs.");
    }
}
