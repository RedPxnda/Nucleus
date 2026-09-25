package com.redpxnda.nucleus.widgets.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class State<T> {

    private T value;

    private final List<Subscription> listeners = new ArrayList<>();

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
        for (Subscription listener : List.copyOf(listeners))
            listener.update(value);
    }

    public Subscription subscribe(Consumer<T> listener, Consumer<Subscription> removeFromListener) {
        listener.accept(get());
        State<T>.Subscription sub = new Subscription(listener, removeFromListener);
        listeners.add(sub);
        return sub;
    }

    public List<Subscription> getListeners() {
        return List.copyOf(listeners);
    }

    public void unSubscribeAll() {
        getListeners().forEach(Subscription::unsubscribe);
    }

    public class Subscription {
        private final Consumer<T> callback;
        private final Consumer<Subscription> removeFromListener;

        protected Subscription(Consumer<T> onChange, Consumer<Subscription> removeFromListener) {
            this.callback = onChange;
            this.removeFromListener = removeFromListener;
        }

        public State<T> getState() {
            return State.this;
        }

        public void update(T data) {
            callback.accept(data);
        }

        public void unsubscribe() {
            listeners.remove(this);
            removeFromListener.accept(this);
        }
    }
}