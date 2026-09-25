package com.redpxnda.nucleus.widgets.state;

import java.util.function.Consumer;

/**
 * interface for new state system
 */
@SuppressWarnings("unused")
public interface StateSubscriber {
    <T> State<T>.Subscription subscribeTo(State<T> state, Consumer<T> onChange);

    void unSubscribe(State<?> state);

    void unSubScribeAll();
}