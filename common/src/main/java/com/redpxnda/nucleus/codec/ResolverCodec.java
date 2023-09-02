package com.redpxnda.nucleus.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.resolving.Resolver;

import java.util.function.Function;

public class ResolverCodec<E, T extends Resolver<E>> implements Codec<T> {
    protected final Function<String, T> creator;

    public ResolverCodec(Function<String, T> creator) {
        this.creator = creator;
    }

    @Override
    public <J> DataResult<Pair<T, J>> decode(DynamicOps<J> ops, J input) {
        return Codec.STRING.decode(ops, input).map(pair -> Pair.of(creator.apply(pair.getFirst()), pair.getSecond()));
    }

    @Override
    public <J> DataResult<J> encode(T input, DynamicOps<J> ops, J prefix) {
        return DataResult.success(ops.createString(input.getBase()));
    }
}
