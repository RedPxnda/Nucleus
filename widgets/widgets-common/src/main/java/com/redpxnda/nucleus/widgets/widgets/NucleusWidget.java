package com.redpxnda.nucleus.widgets.widgets;

import com.redpxnda.nucleus.widgets.state.State;
import com.redpxnda.nucleus.widgets.state.StateSubscriber;
import com.redpxnda.nucleus.widgets.world.WorldWidget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Full implementation of all features.
 * includes {@link com.redpxnda.nucleus.widgets.state.UiAttachable}
 * includes {@link com.redpxnda.nucleus.widgets.state.StateSubscriber}
 * includes {@link com.redpxnda.nucleus.widgets.controller.ControllerNavigable}
 * full support for hierarchy, states, detachment support and controller support
 */
@SuppressWarnings("unused")
public abstract class NucleusWidget extends BasicControllerNavigableWidget implements WorldWidget, StateSubscriber {
    private final List<State<?>.Subscription> subscriptions = new ArrayList<>();
    protected NucleusWidget(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
    }


    public <T> State<T>.Subscription subscribeTo(State<T> state, Consumer<T> onChange) {
        State<T>.Subscription subscription = state.subscribe(onChange, subscriptions::remove);
        subscriptions.add(subscription);
        return subscription;
    }

    public void unSubScribeAll() {
        for (State<?>.Subscription subscription : subscriptions) {
            subscription.unsubscribe();
        }
        subscriptions.clear();
    }

    public void unSubscribe(State<?> state) {
        subscriptions.stream()
                .filter(subscription -> subscription.getState() == state)
                .forEach(State.Subscription::unsubscribe);
    }
}
