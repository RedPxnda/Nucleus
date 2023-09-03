package com.redpxnda.nucleus.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.resolving.Resolver;
import com.redpxnda.nucleus.util.MiscUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ResolverCodec<E, T extends Resolver<E>> implements Codec<T> {
    protected final Function<String, T> creator;
    protected final @Nullable Codec<E> direct;

    public ResolverCodec(Function<String, T> creator) {
        this.creator = creator;
        this.direct = null;
    }
    public ResolverCodec(Function<String, T> creator, @NotNull Codec<E> direct) {
        this.creator = creator;
        this.direct = direct;
    }

    @Override
    public <J> DataResult<Pair<T, J>> decode(DynamicOps<J> ops, J input) {
        var result = Codec.STRING.decode(ops, input).map(pair -> Pair.of(creator.apply(pair.getFirst()), pair.getSecond()));
        if (direct == null || result.result().isPresent()) return result;
        return direct.decode(ops, input).map(pair ->
                Pair.of(
                        MiscUtil.initialize(creator.apply(""), o -> o.setDefiniteAnswer(pair.getFirst())),
                        pair.getSecond()
                ));
    }

    @Override
    public <J> DataResult<J> encode(T input, DynamicOps<J> ops, J prefix) {
        return DataResult.success(ops.createString(input.getBase()));
    }
}
