package com.redpxnda.nucleus.datapack.json.annotation;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.datapack.references.Reference;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class ContextSettings<C extends Reference<?>, A> {
    private final BiFunction<C, A, ?> func;
    private final @Nullable Codec<A> codec;
    private final @Nullable Class<?> clazz;

    public ContextSettings(BiFunction<C, A, Object> func, @Nullable Codec<A> codec) {
        this.clazz = null;
        this.func = func;
        this.codec = codec;
    }
    public <T> ContextSettings(@Nullable Class<T> clazz, BiFunction<C, A, Reference<T>> func, @Nullable Codec<A> codec) {
        this.clazz = clazz;
        this.func = func;
        this.codec = codec;
    }

    public BiFunction<C, A, ?> getFunc() {
        return func;
    }

    public @Nullable Codec<A> getCodec() {
        return codec;
    }

    public @Nullable Class<?> getRefClass() {
        return clazz;
    }
}
