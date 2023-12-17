package com.redpxnda.nucleus.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class JsonCommentsCodec<T> implements Codec<T> {
    protected final Codec<T> delegate;

    public JsonCommentsCodec(Codec<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return delegate.decode(ops, input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        DataResult<T1> result = delegate.encode(input, ops, prefix);
//        if (result.result().isPresent() && result.result().get() instanceof O) {
//        }
        return null;
    }
}
