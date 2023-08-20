package com.redpxnda.nucleus.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ObjectWrapper<T> implements Wrapper<T> {
    public final List<Class<? super T>> parents = new ArrayList<>();

    public ObjectWrapper() {

    }

    /**
     * Denotes that this wrapper should inherit methods from one or more other classes.
     */
    @SafeVarargs
    public final void extend(Class<? super T> clz, Class<? super T>... classes) {
        parents.add(clz);
        parents.addAll(Arrays.asList(classes));
    }

    @Override
    public Object get(T instance, String key) {
        return null;
    }

    public @interface Alias {
        String[] value() default {};
    }
    public @interface Doc {
        String value();
    }
    public @interface Ignore {}
}
