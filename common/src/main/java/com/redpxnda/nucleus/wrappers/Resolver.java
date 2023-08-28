package com.redpxnda.nucleus.wrappers;

import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolverCodec;

import java.util.HashMap;
import java.util.Map;

@AutoCodec.Override("codecGetter")
public class Resolver<T> {
    public static AutoCodec.CodecGetter<Resolver> rawCodecGetter = params -> {
        Class<?> cls = params != null && params.length == 1 ? params[0] instanceof Class<?> c ? c : Object.class : Object.class;
        return new ResolverCodec<>(str -> new Resolver<>(cls, str));
    };
    public static AutoCodec.CodecGetter<Resolver> codecGetter = params -> {
        Class<?> cls;
        if (params != null && params.length == 1) {
            if (String.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forString);
            if (Double.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forDouble);
            if (Float.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forFloat);
            if (Boolean.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forBoolean);
            if (Integer.class.equals(params[0])) return new ResolverCodec<>(VariabledResolver::forInteger);
            cls = params[0] instanceof Class<?> c ? c : Object.class;
        } else cls = Object.class;
        return new ResolverCodec<>(str -> new Resolver<>(cls, str));
    };

    protected String base;
    protected final String[] segments;
    protected final Class<T> clazz;
    protected final Map<String, WrapperHolder<?>> permanentContexts = new HashMap<>();
    protected final Map<String, WrapperHolder<?>> temporaryContexts = new HashMap<>();

    /**
     * @param outputClass   the class of the output
     * @param string        the expression string
     * @param setupSegments whether to split the string into segments or not
     */
    protected Resolver(Class<T> outputClass, String string, boolean setupSegments) {
        base = string;
        segments = setupSegments ? base.split("\\.") : null;
        clazz = outputClass;
    }

    public Resolver(Class<T> outputClass, String string) {
        this(outputClass, string, true);
    }

    protected <A> Wrapper<A> getWrapperFor(A instance) {
        return instance == null ? Wrapper.emptyWrapper : (Wrapper<A>) Wrappers.get(instance.getClass());
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

    // override this resolve method and no other resolve methods for subclasses
    public T resolve() {
        temporaryContexts.putAll(permanentContexts);

        int index = -1;

        Object object = null;
        Wrapper<Object> wrapper = null;

        for (String part : segments) {
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

            object = wrapper.invoke(object, part);
            wrapper = getWrapperFor(object);
        }

        resetEphemerals();

        if (object != null && !clazz.isAssignableFrom(object.getClass()))
            throw new RuntimeException("Unexpected output for Resolver! Expected '%s' but received '%s' instead!".formatted(clazz, object.getClass()));

        return (T) object;
    }

    public record WrapperHolder<T>(Wrapper<T> wrapper, T instance) {}
}
