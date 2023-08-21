package com.redpxnda.nucleus.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.util.Defaulted;

public class DefaultedCodec<V> implements Codec<Defaulted<V>> {
    private final V def;
    private final Codec<V> codec;

    public DefaultedCodec(V def, Codec<V> codec) {
        this.def = def;
        this.codec = codec;
    }

    @Override
    public <T> DataResult<Pair<Defaulted<V>, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<V> result = codec.parse(ops, input);
        if (result.result().isPresent()) return DataResult.success(Pair.of(Defaulted.of(def).makeValue(result.result().get()), input));
        return DataResult.success(Pair.of(Defaulted.of(def), input));
    }

    @Override
    public <T> DataResult<T> encode(Defaulted<V> input, DynamicOps<T> ops, T prefix) {
        return codec.encode(input.get(), ops, prefix);
    }
}
