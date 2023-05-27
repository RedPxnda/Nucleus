package com.redpxnda.nucleus.datapack.json.types;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.List;

public class ChainedCondition<T> {
    public static <T> Codec<ChainedCondition<T>> codec(Codec<T> codec) {
        return Codec.either(Condition.codec(codec), codec.xmap(t -> new Condition<>(new Evaluable("true"), t), c -> c.result)).listOf().xmap(list ->
                new ChainedCondition<>(list.stream()
                .map(i -> i.left().isPresent() ? i.left().get() : i.right().get())
                .toList()),
                c -> c.chain.stream().map(Either::<Condition<T>, Condition<T>>left).toList());
    }

    public final List<Condition<T>> chain;

    public ChainedCondition(List<Condition<T>> chain) {
        this.chain = chain;
    }
}
