package com.redpxnda.nucleus.codec.behavior;

import com.redpxnda.nucleus.Nucleus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BehaviorOutline<B extends TypeBehaviorGetter<?, ?>, A extends AnnotationBehaviorGetter<?, ?>> {
    private static final Logger LOGGER = Nucleus.getLogger();
    public final Map<Class<?>, B> statics = new HashMap<>();
    public final Map<Class<? extends Annotation>, A> annotators = new HashMap<>();
    public final List<B> dynamics = new ArrayList<>();
    public final boolean enableFieldCaching;
    public final boolean enableTypeCaching;
    public final Map<Field, Object> fieldCache = new HashMap<>();
    public final Map<Type, Object> typeCache = new HashMap<>();

    public BehaviorOutline() {
        this.enableFieldCaching = true;
        this.enableTypeCaching = true;
    }

    public BehaviorOutline(boolean enableCaching) {
        this.enableFieldCaching = enableCaching;
        this.enableTypeCaching = enableCaching;
    }

    public BehaviorOutline(boolean enableFieldCaching, boolean enableTypeCaching) {
        this.enableFieldCaching = enableFieldCaching;
        this.enableTypeCaching = enableTypeCaching;
    }

    public @Nullable Object get(Type type, boolean isRoot) {
        Type[] params = null;
        Class cls;

        if (type instanceof ParameterizedType pt) {
            params = pt.getActualTypeArguments();
            cls = (Class) pt.getRawType();
        } else if (type instanceof Class c) cls = c;
        else {
            LOGGER.error("Type used for a Behavior cannot be (or often even contain) Wildcards or TypeParameters! (must be a ParameterizedType or Class). Type: {}", type);
            throw new IllegalStateException("Type used for a Behavior attempted to use Wildcard/TypeParameter. See logger error above.");
        }

        return get(null, cls, type, params, isRoot);
    }

    public @Nullable Object get(Field field, boolean isRoot) {
        Type raw = field.getGenericType();
        Type[] params = raw instanceof ParameterizedType pt ? pt.getActualTypeArguments() : null;
        Class cls = field.getType();
        return get(field, cls, raw, params, isRoot);
    }

    public @Nullable Object get(@Nullable Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        if (enableFieldCaching) {
            if (field != null) {
                Object value;
                if ((value = fieldCache.get(field)) == null) {
                    value = getWithoutCache(field, cls, raw, params, isRoot);
                    fieldCache.put(field, value);
                }
                return value;
            }
        }

        if (enableTypeCaching) {
            Object value;
            if ((value = typeCache.get(raw)) == null) {
                value = getWithoutCache(null, cls, raw, params, isRoot);
                typeCache.put(raw, value);
            }
            return value;
        }

        return getWithoutCache(field, cls, raw, params, isRoot);
    }

    public @Nullable Object getWithoutCache(@Nullable Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot) {
        if (field != null) {
            for (Annotation annotation : field.getAnnotations()) {
                AnnotationBehaviorGetter annotator = annotators.get(annotation.annotationType());
                if (annotator != null) {
                    Object result = annotator.get(annotation, field, cls, raw, params, isRoot);
                    if (result != null) return result;
                }
            }
        }

        for (B dynamic : dynamics) {
            Object result = dynamic.get(field, cls, raw, params, isRoot);
            if (result != null) return result;
        }

        var st = statics.get(cls);
        return st == null ? null : st.get(field, cls, raw, params, isRoot);
    }
}
