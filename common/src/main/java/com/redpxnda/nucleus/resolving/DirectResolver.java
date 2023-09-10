package com.redpxnda.nucleus.resolving;

import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolverCodec;
import com.redpxnda.nucleus.resolving.wrappers.Wrapper;
import com.redpxnda.nucleus.resolving.wrappers.WrapperHolder;
import org.intellij.lang.annotations.RegExp;

import java.util.Arrays;

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
    public DirectResolver(Class<T> outputClass, String base) {
        this(outputClass, base, "\\.");
    }

    public DirectResolver(Class<T> outputClass, String base, @RegExp String segmentsRegex) {
        super(outputClass, base);
        splitRegex = segmentsRegex;
    }

    @Override
    protected T calculate() {
        String[] parts = resolved.split(splitRegex);
        if (parts.length < 1) return null;

        WrapperHolder<?> holder = temporaryContexts.get(parts[0]);
        Object object = evaluateObjectCallTree(Arrays.copyOfRange(parts, 1, parts.length), resolved, (Wrapper) holder.wrapper(), holder.instance());

        /*Object object = null;
        Wrapper<Object> wrapper = null;

        for (String part : ) {
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
        }*/

        if (object != null && !clazz.isAssignableFrom(object.getClass()))
            throw new RuntimeException("Unexpected output for Resolver! Expected '%s' but received '%s' instead!".formatted(clazz, object.getClass()));
        return (T) object;
    }
}
