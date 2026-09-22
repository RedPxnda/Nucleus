package com.redpxnda.nucleus.widgets.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class State<T> {

    private T value;

    private final List<Consumer<T>> listeners = new ArrayList<>();

    public State(T initialValue) {
        this.value = initialValue;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        if (Objects.equals(this.value, value))
            return;
        this.value = value;
        for (Consumer<T> listener : List.copyOf(listeners))
            listener.accept(value);
    }

    public void update(StateSubscriber stateSubscriber) {
        stateSubscriber.onStateChanged(this, value);
    }

    public Subscription subscribe(Consumer<T> listener) {
        listeners.add(listener);
        listener.accept(value);

        return new Subscription() {
            private boolean subscribed = true;

            @Override
            public void unsubscribe() {
                if (!subscribed)
                    return;

                subscribed = false;
                listeners.remove(listener);
            }
        };
    }

    public interface Subscription {
        void unsubscribe();
    }
}