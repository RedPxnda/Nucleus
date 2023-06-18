package com.redpxnda.nucleus.datapack.codec;

import com.mojang.serialization.Codec;

import java.util.function.Function;

public class EnumCodec {
    public static <T extends Enum<T>> Codec<T> of(Class<T> cls) {
        return Codec.STRING.xmap(s -> T.valueOf(cls, s), Enum::name);
    }

    public static <T extends Enum<T>> Codec<T> of(Class<T> cls, Function<String, T> convert) {
        return Codec.STRING.xmap(convert, Enum::name);
    }
}
