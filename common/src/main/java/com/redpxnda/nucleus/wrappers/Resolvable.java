package com.redpxnda.nucleus.wrappers;

import com.google.common.reflect.TypeToken;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolvableCodec;

import java.util.HashMap;
import java.util.Map;

@AutoCodec.Override("codecGetter")
public class Resolvable<T> {
    public static AutoCodec.CodecGetter<Resolvable> codecGetter = params -> {
        if (params != null && params.length == 1) {
            if (String.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forString);
            if (Double.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forDouble);
            if (Float.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forFloat);
            if (Boolean.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forBoolean);
            if (Integer.class.equals(params[0])) return new ResolvableCodec<>(VariabledResolvable::forInteger);
        }
        return new ResolvableCodec<>(Resolvable::new);
    };

    protected String base;
    protected final Map<String, WrapperHolder<?>> permanentContexts = new HashMap<>();
    protected final Map<String, WrapperHolder<?>> temporaryContexts = new HashMap<>();

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

    public <A> void provideTemporary(String name, A instance) {
        provideTemporary(name, getWrapperFor(instance), instance);
    }

    public <A> void provideTemporary(String name, Wrapper<A> wrapper, A instance) {
        WrapperHolder<A> holder = new WrapperHolder<>(wrapper, instance);
        temporaryContexts.put(name, holder);
    }

    public void provideTemporary(String name, Number instance) {
        provideTemporary(name, null, instance);
    }
    public void provideTemporary(String name, boolean instance) {
        provideTemporary(name, null, instance);
    }
    public void provideTemporary(String name, String instance) {
        provideTemporary(name, null, instance);
    }

    public String getBase() {
        return base;
    }

    public void resetEphemerals() {
        temporaryContexts.clear();
        temporaryContexts.putAll(permanentContexts);
    }

    public T resolve() {
        temporaryContexts.putAll(permanentContexts);

        String[] parts = base.split("\\.");
        int index = -1;

        Object object = null;
        Wrapper<Object> wrapper = null;

        for (String part : parts) {
            index++;
            if (index == 0) {
                WrapperHolder<?> holder = temporaryContexts.get(part);
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
