package com.redpxnda.nucleus.resolving;

public record WrapperHolder<T>(Wrapper<T> wrapper, T instance) {
}
