package com.redpxnda.nucleus.resolving.wrappers;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

public class AutoWrapper<T> implements Wrapper<T> {
    public final Map<String, Function<T, ?>> methods;

    public AutoWrapper(Map<String, Function<T, ?>> methods) {
        this.methods = methods;
    }

    @Override
    public Object customInvoke(@NotNull T instance, String key) {
        Function<T, ?> func = methods.get(key);
        if (func != null) return func.apply(instance);
        return null;
    }
}
