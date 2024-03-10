package com.redpxnda.nucleus.codec.behavior;

import com.redpxnda.nucleus.Nucleus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BiBehaviorOutline<B extends TypeBehaviorGetter.Bi<?, ?, ?>> extends BehaviorOutline<B> {
    private static final Logger LOGGER = Nucleus.getLogger();
    public final boolean enableSecondaryFieldCaching;
    public final boolean enableSecondaryTypeCaching;
    public final Map<Field, Object> secondaryFieldCache = new ConcurrentHashMap<>();
    public final Map<Type, Object> secondaryTypeCache = new ConcurrentHashMap<>();

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

    public @Nullable Object getSecondary(Type type, List<String> passes, String key) {
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

        return getSecondary(null, cls, type, params, passes, key);
    }

    public @Nullable Object getSecondary(Field field, List<String> passes, String key) {
        Type raw = field.getGenericType();
        Type[] params = raw instanceof ParameterizedType pt ? pt.getActualTypeArguments() : null;
        Class cls = field.getType();
        return getSecondary(field, cls, raw, params, passes, key);
    }

    public @Nullable Object getSecondary(@Nullable Field field, Class cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
        if (enableSecondaryFieldCaching) {
            if (field != null) {
                Object value;
                if ((value = secondaryFieldCache.get(field)) == null) {
                    value = getSecondaryWithoutCache(field, cls, raw, params, passes, key);
                    secondaryFieldCache.put(field, value);
                }
                return value;
            }
        }

        if (enableSecondaryTypeCaching) {
            Object value;
            if ((value = secondaryTypeCache.get(raw)) == null) {
                value = getSecondaryWithoutCache(null, cls, raw, params, passes, key);
                secondaryTypeCache.put(raw, value);
            }
            return value;
        }
        return getSecondaryWithoutCache(field, cls, raw, params, passes, key);
    }

    public @Nullable Object getSecondaryWithoutCache(@Nullable Field field, Class cls, Type raw, @Nullable Type[] params, List<String> passes, String key) {
        synchronized (dynamics) {
            dynamics.sortIfUnsorted();
            for (B dynamic : dynamics.keySet()) {
                Object result = dynamic.getSecondary(field, cls, raw, params, passes, key);
                if (result != null) return result;
            }
        }

        var st = statics.get(cls);
        return st == null ? null : st.getSecondary(field, cls, raw, params, passes, key);
    }
}
