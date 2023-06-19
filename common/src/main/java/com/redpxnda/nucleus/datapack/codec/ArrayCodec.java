package com.redpxnda.nucleus.datapack.codec;

import com.mojang.serialization.Codec;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ArrayCodec {
    public static <T> Codec<T[]> of(Codec<T> codec, IntFunction<T[]> converter) {
        return codec.listOf().xmap(l -> l.toArray(converter), Arrays::asList);
    }
}
