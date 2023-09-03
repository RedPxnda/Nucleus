package com.redpxnda.nucleus.resolving;

import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolverCodec;
import org.intellij.lang.annotations.RegExp;

@AutoCodec.Override("codecGetter")
public class DirectResolver<T> extends Resolver<T> {
    public static final AutoCodec.CodecGetter<DirectResolver> codecGetter = params -> {
        Class<?> cls = params != null && params.length == 1 ? params[0] instanceof Class<?> c ? c : Object.class : Object.class;
        return new ResolverCodec<>(str -> new DirectResolver<>(cls, str));
    };

    protected final String splitRegex;

    /**
     * @param outputClass   the class of the output
     * @param base        the expression string
     */
    protected DirectResolver(Class<T> outputClass, String base) {
        this(outputClass, base, "\\.");
    }

    protected DirectResolver(Class<T> outputClass, String base, @RegExp String segmentsRegex) {
        super(outputClass, base);
        splitRegex = segmentsRegex;
    }

    @Override
    protected T calculate() {
        int index = -1;

        Object object = null;
        Wrapper<Object> wrapper = null;

        for (String part : resolved.split(splitRegex)) {
            index++;
            if (index == 0) {
                WrapperHolder<?> holder = temporaryContexts.get(part);
                if (holder == null) {
                    throw new RuntimeException("Failed to get context '" + part + "' from Resolvable! Unprovided?");
                }

                object = holder.instance();
                wrapper = (Wrapper<Object>) holder.wrapper();
                continue;
            }

            object = wrapper.invoke(object, part);
            wrapper = getWrapperFor(object);
        }

        if (object != null && !clazz.isAssignableFrom(object.getClass()))
            throw new RuntimeException("Unexpected output for Resolver! Expected '%s' but received '%s' instead!".formatted(clazz, object.getClass()));
        return (T) object;
    }
}
