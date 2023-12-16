package com.redpxnda.nucleus.event;

import com.google.common.reflect.AbstractInvocationHandler;
import com.redpxnda.nucleus.util.PriorityMap;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventResult;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * An extension of Architectury's Event, with a priority system.
 * Lowest priority fires first.
 * <p></p>
 * NOTE: all listeners must be added BEFORE the event fires, otherwise the listeners
 * will not be fired in the correct order. If this is not achievable, run the sort
 * method to resort the listeners whenever needed.
 */
public interface PrioritizedEvent<T> extends Event<T> {
    void register(T listener, float prio);
    boolean hasBeenSorted();
    void sort();

    static <T> PrioritizedEvent<T> of(Function<PriorityMap<T>, T> function) {
        return new Impl<>(function);
    }

    @SafeVarargs
    static <T> PrioritizedEvent<T> createLoop(T... typeGetter) {
        if (typeGetter.length != 0) throw new IllegalStateException("Type getter array must be empty!");
        return createLoop((Class<T>) typeGetter.getClass().getComponentType());
    }
    static <T> PrioritizedEvent<T> createLoop(Class<T> cls) {
        return of(listeners -> (T) Proxy.newProxyInstance(PrioritizedEvent.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (T listener : listeners.keySet()) {
                    Impl.invokeMethod(listener, method, args);
                }
                return null;
            }
        }));
    }

    @SafeVarargs
    static <T> PrioritizedEvent<T> createObject(T... typeGetter) { // return null to pass, return something else to not
        if (typeGetter.length != 0) throw new IllegalStateException("Type getter array must be empty!");
        return createObject((Class<T>) typeGetter.getClass().getComponentType());
    }
    static <T> PrioritizedEvent<T> createObject(Class<T> cls) {
        return of(listeners -> (T) Proxy.newProxyInstance(PrioritizedEvent.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (T listener : listeners.keySet()) {
                    Object result = Impl.invokeMethod(listener, method, args);
                    if (result != null) return result;
                }
                return null;
            }
        }));
    }

    @SafeVarargs
    static <T> PrioritizedEvent<T> createEventResult(T... typeGetter) {
        if (typeGetter.length != 0) throw new IllegalStateException("Type getter array must be empty!");
        return createEventResult((Class<T>) typeGetter.getClass().getComponentType());
    }
    static <T> PrioritizedEvent<T> createEventResult(Class<T> cls) {
        return of(listeners -> (T) Proxy.newProxyInstance(PrioritizedEvent.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (T listener : listeners.keySet()) {
                    EventResult result = Objects.requireNonNull(Impl.invokeMethod(listener, method, args));
                    if (result.interruptsFurtherEvaluation()) return result;
                }
                return EventResult.pass();
            }
        }));
    }

    @SafeVarargs
    static <T> PrioritizedEvent<T> createBoolean(T... typeGetter) {
        if (typeGetter.length != 0) throw new IllegalStateException("Type getter array must be empty!");
        return createBoolean((Class<T>) typeGetter.getClass().getComponentType());
    }
    static <T> PrioritizedEvent<T> createBoolean(Class<T> cls) {
        return of(listeners -> (T) Proxy.newProxyInstance(PrioritizedEvent.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (T listener : listeners.keySet()) {
                    boolean result = Objects.requireNonNull(Impl.invokeMethod(listener, method, args));
                    if (!result) return false;
                }
                return EventResult.pass();
            }
        }));
    }

    @SafeVarargs
    static <T> PrioritizedEvent<T> createCompoundEventResult(T... typeGetter) {
        if (typeGetter.length != 0) throw new IllegalStateException("Type getter array must be empty!");
        return createCompoundEventResult((Class<T>) typeGetter.getClass().getComponentType());
    }
    static <T> PrioritizedEvent<T> createCompoundEventResult(Class<T> cls) {
        return of(listeners -> (T) Proxy.newProxyInstance(PrioritizedEvent.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                for (T listener : listeners.keySet()) {
                    CompoundEventResult<?> result = Objects.requireNonNull(Impl.invokeMethod(listener, method, args));
                    if (result.interruptsFurtherEvaluation()) return result;
                }
                return CompoundEventResult.pass();
            }
        }));
    }

    @SafeVarargs
    static <T, R> PrioritizedEvent<T> createDynamic(Function<List<R>, R> combiner, T... typeGetter) {
        if (typeGetter.length != 0) throw new IllegalStateException("Type getter array must be empty!");
        return createDynamic(combiner, (Class<T>) typeGetter.getClass().getComponentType());
    }
    static <T, R> PrioritizedEvent<T> createDynamic(Function<List<R>, R> combiner, Class<T> cls) {
        return of(listeners -> (T) Proxy.newProxyInstance(PrioritizedEvent.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                List<R> results = new ArrayList<>();
                for (T listener : listeners.keySet()) {
                    R result = Impl.invokeMethod(listener, method, args);
                    results.add(result);
                }
                return combiner.apply(results);
            }
        }));
    }

    @SafeVarargs
    static <T, R extends Interruptable> PrioritizedEvent<T> createInterruptable(Function<List<R>, R> combiner, T... typeGetter) {
        if (typeGetter.length != 0) throw new IllegalStateException("Type getter array must be empty!");
        return createInterruptable(combiner, (Class<T>) typeGetter.getClass().getComponentType());
    }
    static <T, R extends Interruptable> PrioritizedEvent<T> createInterruptable(Function<List<R>, R> combiner, Class<T> cls) {
        return of(listeners -> (T) Proxy.newProxyInstance(PrioritizedEvent.class.getClassLoader(), new Class[]{cls}, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                List<R> results = new ArrayList<>();
                for (T listener : listeners.keySet()) {
                    R result = Impl.invokeMethod(listener, method, args);
                    if (result.isOverwritten()) return result;
                    results.add(result);
                    if (result.isInterrupted()) break;
                }
                return combiner.apply(results);
            }
        }));
    }

    /**
     * An interface representing an object that can interrupt or overwrite the evaluation of an event.
     * <p></p>
     * Interrupting the event stops it and prevents it from being passed to subsequent listeners.
     * It then sends the collected results(every value returned by every listener) into the combiner function defined by the event.
     * <p></p>
     * Overwriting the event acts much like interrupting the event, but instead of sending the collected results
     * to the combiner function, it makes the event return this Interruptable object itself.
     */
    interface Interruptable {
        boolean isInterrupted();
        boolean isOverwritten();
    }

    class Impl<T> implements PrioritizedEvent<T> {
        private static <T, R> R invokeMethod(T listener, Method method, Object[] args) throws Throwable {
            return (R) MethodHandles.lookup().unreflect(method)
                    .bindTo(listener).invokeWithArguments(args);
        }

        protected final Function<PriorityMap<T>, T> function;
        protected final PriorityMap<T> listeners = new PriorityMap<>();
        protected T invoker = null;

        protected Impl(Function<PriorityMap<T>, T> function) {
            this.function = function;
        }

        @Override
        public void register(T listener, float prio) {
            listeners.put(listener, prio);
        }

        @Override
        public boolean hasBeenSorted() {
            return listeners.hasBeenSorted();
        }

        @Override
        public void sort() {
            listeners.sort();
        }

        @Override
        public T invoker() {
            if (invoker == null) update();
            return invoker;
        }

        @Override
        public void register(T listener) {
            listeners.put(listener, 0f);
        }

        @Override
        public void unregister(T listener) {
            listeners.remove(listener);
        }

        @Override
        public boolean isRegistered(T listener) {
            return listeners.containsKey(listener);
        }

        @Override
        public void clearListeners() {
            listeners.clear();
        }

        public void update() {
            listeners.sortIfUnsorted();
            invoker = listeners.size() == 1 ? listeners.keySet().stream().findFirst().get() : function.apply(listeners);
        }
    }

}
