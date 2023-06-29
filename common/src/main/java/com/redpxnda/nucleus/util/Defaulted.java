package com.redpxnda.nucleus.util;

import org.jetbrains.annotations.Nullable;

public class Defaulted<T> {
    private @Nullable T value;
    private final T def;

    public Defaulted(T def) {
        this.def = def;
    }
    public static <T> Defaulted<T> of(T def) {
        return new Defaulted<>(def);
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Defaulted<T> makeValue(T value) {
        this.value = value;
        return this;
    }

    public boolean isPresent() {
        return value == null;
    }

    public T get() {
        if (value == null) return def;
        return value;
    }
    public T getDefault() {
        return def;
    }

    @Override
    public String toString() {
        return "Defaulted{" +
                "value=" + value +
                ", def=" + def +
                '}';
    }
}
