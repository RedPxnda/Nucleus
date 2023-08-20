package com.redpxnda.nucleus.wrappers;

public interface Wrapper<T> {
    Object get(T instance, String key);
}
