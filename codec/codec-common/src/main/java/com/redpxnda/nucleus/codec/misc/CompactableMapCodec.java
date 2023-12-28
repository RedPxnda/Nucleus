package com.redpxnda.nucleus.codec.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.UnboundedMapCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A codec representing a map, but in two ways: <br>
 * <code>
 *     {
 *         "someKey": "someValue"
 *     }
 * </code>
 * OR
 * <code>["someKey|someValue"]</code> (but will encode as the former) <br>
 * The "converter" represents a way to convert a string into your value type.
 */
public class CompactableMapCodec<K, V> implements Codec<Map<K, V>> {
    public static <K, V> CompactableMapCodec<K, V> of(Codec<K> keyCodec, Codec<V> valueCodec, Function<String, K> keyConverter, Function<String, V> valueConverter) {
        return new CompactableMapCodec<>(keyCodec, valueCodec, keyConverter, valueConverter);
    }

    protected final Codec<K> keyCodec;
    protected final Codec<V> valueCodec;
    protected final Function<String, K> keyConverter;
    protected final Function<String, V> valueConverter;
    protected final UnboundedMapCodec<K, V> delegate;

    public CompactableMapCodec(Codec<K> keyCodec, Codec<V> valueCodec, Function<String, K> keyConverter, Function<String, V> valueConverter) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
        this.delegate = Codec.unboundedMap(keyCodec, valueCodec);
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        var potentialMap = delegate.decode(ops, input);
        if (potentialMap.result().isPresent()) return DataResult.success(potentialMap.result().get());

        var potentialStream = ops.getStream(input);
        if (potentialStream.result().isPresent()) {
            Map<K, V> map = new HashMap<>();
            Stream<T> stream = potentialStream.result().get();

            List<T> fails = new ArrayList<>();
            stream.forEach(t -> {
                var potentialString = ops.getStringValue(t);
                if (potentialString.result().isEmpty()) fails.add(t);
                String[] sections = potentialString.result().get().split("\\|", 2);
                if (sections.length != 2) {
                    fails.add(t);
                    return;
                }
                map.put(keyConverter.apply(sections[0]), valueConverter.apply(sections[1]));
            });

            if (!fails.isEmpty())
                return DataResult.error(() -> "Found invalid entries for compact map: Either not a string, or invalid format! -> " + fails, Pair.of(map, input));
            return DataResult.success(Pair.of(map, input));
        }

        return DataResult.error(() -> "Could not decode compactable map: Not an array or map! -> " + input);
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        return delegate.encode(input, ops, prefix);
    }
}
