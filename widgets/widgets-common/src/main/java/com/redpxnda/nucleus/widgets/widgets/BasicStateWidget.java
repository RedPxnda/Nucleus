package com.redpxnda.nucleus.widgets.widgets;

import com.redpxnda.nucleus.widgets.state.State;
import com.redpxnda.nucleus.widgets.state.StateSubscriber;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class implements basic State and onStateChangeReaction
 * this is package private because its just one feature of the full {@link NucleusWidget}
 */
@SuppressWarnings("unused")
abstract class BasicStateWidget extends BasicAttachAbleWidget implements StateSubscriber {
    private final List<State<?>> states = new ArrayList<>();
    private final List<State.Subscription> subscriptions = new ArrayList<>();

    protected BasicStateWidget(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
    }

    public void subscribeTo(State<?> state) {
        if (states.contains(state))
            return;
        states.add(state);
        State.Subscription subscription =
                state.subscribe(value -> state.update(this));
        subscriptions.add(subscription);
    }

    public <T> State.Subscription subscribeTo(State<T> state, Consumer<T> onChange) {
        State.Subscription subscription = state.subscribe(t -> {
            onChange.accept(t);
            onStateChanged(state, t);
        });
        subscriptions.add(subscription);
        return subscription;
    }

    public void unsubscribeAll() {
        for (State.Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }

        subscriptions.clear();
        states.clear();
    }


    /**
     * Called whenever any of the subscribed States changes.
     */
    public <T> void onStateChanged(State<T> state, T value) {
        if (!isAttached()) {
            unsubscribeAll();
        }
    }
}