package com.redpxnda.nucleus.codec.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.BaseMapCodec;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomMapCodec<K, V, M extends Map<K, V>> implements Codec<M>, BaseMapCodec<K, V> {
    protected final Codec<K> keyCodec;
    protected final Codec<V> valueCodec;
    protected final Function<Map<K, V>, M> mapCreator;

    protected CustomMapCodec(Codec<K> keyCodec, Codec<V> valueCodec, Function<Map<K, V>, M> mapCreator) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.mapCreator = mapCreator;
    }
    public static <K, V, M extends Map<K, V>> CustomMapCodec<K, V, M> of(Codec<K> keyCodec, Codec<V> valueCodec, Function<Map<K, V>, M> creator) {
        return new CustomMapCodec<>(keyCodec, valueCodec, creator);
    }
    public static <K, V, M extends Map<K, V>> CustomMapCodec<K, V, M> of(Codec<K> keyCodec, Codec<V> valueCodec, Supplier<M> creator) {
        return new CustomMapCodec<>(keyCodec, valueCodec, map -> {
            M m = creator.get();
            m.putAll(map);
            return m;
        });
    }

    @Override
    public Codec<K> keyCodec() {
        return keyCodec;
    }

    @Override
    public Codec<V> elementCodec() {
        return valueCodec;
    }

    @Override
    public <T> DataResult<Pair<M, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(mapCreator.apply(r), input));
    }

    @Override
    public <T> DataResult<T> encode(M input, DynamicOps<T> ops, T prefix) {
        return encode(input, ops, ops.mapBuilder()).build(prefix);
    }

    @Override
    public String toString() {
        return "CustomMapCodec[" + keyCodec + " -> " + valueCodec + ']';
    }
}
