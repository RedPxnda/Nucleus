package com.redpxnda.nucleus.datapack.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public class MiscCodecs {
    public static <T> Codec<T[]> array(Codec<T> codec, IntFunction<T[]> converter) {
        return codec.listOf().xmap(l -> l.toArray(converter), Arrays::asList);
    }
    public static <T> Codec<T[]> array(Codec<T> codec, Class<T> cls) {
        return codec.listOf().xmap(l -> l.toArray(i -> (T[]) Array.newInstance(cls, i)), Arrays::asList);
    }

    public static final Codec<DoubleRange> DOUBLE_RANGE = Codec.either(
            Codec.DOUBLE.listOf(),
            Codec.pair(Codec.DOUBLE.fieldOf("min").codec(), Codec.DOUBLE.fieldOf("max").codec())
    )
            .xmap(e -> {
                if (e.left().isPresent()) {
                    List<Double> doubleList = e.left().get();
                    return new DoubleRange(doubleList.get(0), doubleList.get(1));
                } else {
                    Pair<Double, Double> pair = e.right().get();
                    return new DoubleRange(pair.getFirst(), pair.getSecond());
                }
            }, dr -> Either.right(Pair.of(dr.min, dr.max)));

    public record DoubleRange(double min, double max) {}
}
