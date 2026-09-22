package com.redpxnda.nucleus.widgets.state;

import java.util.function.Consumer;

/**
 * interface for new state system
 */
@SuppressWarnings("unused")
public interface StateSubscriber {

    void subscribeTo(State<?> state);

    <T> State.Subscription subscribeTo(State<T> state, Consumer<T> onChange);

    /**
     * Called whenever one of the subscribed States changes.
     */
    <T> void onStateChanged(State<T> state, T value);
}