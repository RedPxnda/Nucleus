package com.redpxnda.nucleus.codec.behavior;

import com.redpxnda.nucleus.Nucleus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BiBehaviorOutline<B extends TypeBehaviorGetter.Bi<?, ?, ?>, A extends AnnotationBehaviorGetter.Bi<?, ?, ?>> extends BehaviorOutline<B, A> {
    private static final Logger LOGGER = Nucleus.getLogger();
    public final boolean enableSecondaryFieldCaching;
    public final boolean enableSecondaryTypeCaching;
    public final Map<Field, Object> secondaryFieldCache = new HashMap<>();
    public final Map<Type, Object> secondaryTypeCache = new HashMap<>();

    public BiBehaviorOutline() {
        super();
        this.enableSecondaryFieldCaching = true;
        this.enableSecondaryTypeCaching = true;
    }

    public BiBehaviorOutline(boolean enableCaching) {
        super(enableCaching);
        this.enableSecondaryFieldCaching = enableCaching;
        this.enableSecondaryTypeCaching = enableCaching;
    }

    public BiBehaviorOutline(boolean enableCaching, boolean enableSecondaryCaching) {
        super(enableCaching);
        this.enableSecondaryFieldCaching = enableSecondaryCaching;
        this.enableSecondaryTypeCaching = enableSecondaryCaching;
    }

    public BiBehaviorOutline(boolean enableFieldCaching, boolean enableTypeCaching, boolean enableSecondaryFieldCaching, boolean enableSecondaryTypeCaching) {
        super(enableFieldCaching, enableTypeCaching);
        this.enableSecondaryFieldCaching = enableSecondaryFieldCaching;
        this.enableSecondaryTypeCaching = enableSecondaryTypeCaching;
    }

    public @Nullable Object getSecondary(Type type, boolean isRoot, String key) {
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

        return getSecondary(null, cls, type, params, isRoot, key);
    }

    public @Nullable Object getSecondary(Field field, boolean isRoot, String key) {
        Type raw = field.getGenericType();
        Type[] params = raw instanceof ParameterizedType pt ? pt.getActualTypeArguments() : null;
        Class cls = field.getType();
        return getSecondary(field, cls, raw, params, isRoot, key);
    }

    public @Nullable Object getSecondary(@Nullable Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
        if (enableSecondaryFieldCaching) {
            if (field != null) {
                Object value;
                if ((value = secondaryFieldCache.get(field)) == null) {
                    value = getSecondaryWithoutCache(field, cls, raw, params, isRoot, key);
                    secondaryFieldCache.put(field, value);
                }
                return value;
            }
        }

        if (enableSecondaryTypeCaching) {
            Object value;
            if ((value = secondaryTypeCache.get(raw)) == null) {
                value = getSecondaryWithoutCache(null, cls, raw, params, isRoot, key);
                secondaryTypeCache.put(raw, value);
            }
            return value;
        }
        return getSecondaryWithoutCache(field, cls, raw, params, isRoot, key);
    }

    public @Nullable Object getSecondaryWithoutCache(@Nullable Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot, String key) {
        if (field != null) {
            for (Annotation annotation : field.getAnnotations()) {
                AnnotationBehaviorGetter.Bi annotator = annotators.get(annotation.annotationType());
                if (annotator != null) {
                    Object result = annotator.getSecondary(annotation, field, cls, raw, params, isRoot, key);
                    if (result != null) return result;
                }
            }
        }

        for (B dynamic : dynamics) {
            Object result = dynamic.getSecondary(field, cls, raw, params, isRoot, key);
            if (result != null) return result;
        }

        var st = statics.get(cls);
        return st == null ? null : st.getSecondary(field, cls, raw, params, isRoot, key);
    }
}
