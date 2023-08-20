package com.redpxnda.nucleus.wrappers;

import com.google.common.reflect.TypeToken;
import com.redpxnda.nucleus.Nucleus;

import java.util.HashMap;
import java.util.Map;

public class Resolvable<T> {
    protected String base;
    protected final Map<String, WrapperHolder<?>> permanentContexts = new HashMap<>();
    protected final Map<String, WrapperHolder<?>> ephemeralContexts = new HashMap<>();

    public Resolvable(String string) {
        base = string;
    }

    protected <A> Wrapper<A> getWrapperFor(A instance) {
        return (Wrapper<A>) Wrappers.get(instance.getClass());
    }

    public <A> void providePermanent(String name, A instance) {
        providePermanent(name, getWrapperFor(instance), instance);
    }

    public <A> void providePermanent(String name, Wrapper<A> wrapper, A instance) {
        WrapperHolder<A> holder = new WrapperHolder<>(wrapper, instance);
        permanentContexts.put(name, holder);
    }

    public void providePermanent(String name, Number instance) {
        providePermanent(name, null, instance);
    }
    public void providePermanent(String name, boolean instance) {
        providePermanent(name, null, instance);
    }
    public void providePermanent(String name, String instance) {
        providePermanent(name, null, instance);
    }

    public <A> void provideEphemeral(String name, A instance) {
        provideEphemeral(name, getWrapperFor(instance), instance);
    }

    public <A> void provideEphemeral(String name, Wrapper<A> wrapper, A instance) {
        WrapperHolder<A> holder = new WrapperHolder<>(wrapper, instance);
        ephemeralContexts.put(name, holder);
    }

    public void provideEphemeral(String name, Number instance) {
        provideEphemeral(name, null, instance);
    }
    public void provideEphemeral(String name, boolean instance) {
        provideEphemeral(name, null, instance);
    }
    public void provideEphemeral(String name, String instance) {
        provideEphemeral(name, null, instance);
    }


    public void resetEphemerals() {
        ephemeralContexts.clear();
        ephemeralContexts.putAll(permanentContexts);
    }

    public T resolve() {
        ephemeralContexts.putAll(permanentContexts);

        String[] parts = base.split("\\.");
        int index = -1;

        Object object = null;
        Wrapper<Object> wrapper = null;

        for (String part : parts) {
            index++;
            if (index == 0) {
                WrapperHolder<?> holder = ephemeralContexts.get(part);
                if (holder == null) {
                    throw new RuntimeException("Failed to get context '" + part + "' from Resolvable! Unprovided?");
                }

                object = holder.instance;
                wrapper = (Wrapper<Object>) holder.wrapper;
                continue;
            }

            object = wrapper.get(object, part);
            wrapper = getWrapperFor(object);
        }

        resetEphemerals();
        try {
            return (T) object;
        } catch (Exception e) {
            TypeToken<T> typeToken = new TypeToken<T>(getClass()) {};
            Nucleus.LOGGER.error("Unexpected output for resolvable! Expected '{}' but recieved '{}' instead!", typeToken.getRawType().getSimpleName(), object.getClass().getSimpleName());
            throw new RuntimeException(e);
        }
    }

    public record WrapperHolder<T>(Wrapper<T> wrapper, T instance) {}
}
