package com.redpxnda.nucleus.resolving.wrappers;

public record WrapperHolder<T>(Wrapper<T> wrapper, T instance) {
}
