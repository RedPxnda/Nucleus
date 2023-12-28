package com.redpxnda.nucleus.codec.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class AdditionalSetupCodec<E, A extends AdditionalSetupCodec.Setup<E>> implements Codec<A> {
    protected final Codec<A> delegate;
    protected final E element;

    public AdditionalSetupCodec(Codec<A> delegate, E element) {
        this.delegate = delegate;
        this.element = element;
    }
    public AdditionalSetupCodec(Codec<A> delegate) {
        this.delegate = delegate;
        this.element = null;
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        var result = delegate.decode(ops, input);
        result.result().ifPresent(pair -> pair.getFirst().additionalSetup(element));
        return result;
    }

    public <T> DataResult<Pair<A, T>> decode(E element, DynamicOps<T> ops, T input) {
        var result = delegate.decode(ops, input);
        result.result().ifPresent(pair -> pair.getFirst().additionalSetup(element));
        return result;
    }

    public <T> DataResult<A> parse(E element, DynamicOps<T> ops, T input) {
        return decode(element, ops, input).map(Pair::getFirst);
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return delegate.encode(input, ops, prefix);
    }

    public interface Setup<E> {
        void additionalSetup(E param);
    }
}
