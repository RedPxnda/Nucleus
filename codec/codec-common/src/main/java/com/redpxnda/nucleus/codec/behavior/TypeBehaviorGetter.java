package com.redpxnda.nucleus.codec.behavior;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public interface TypeBehaviorGetter<R, C> {
    R get(@Nullable Field field, Class<C> cls, Type raw, @Nullable Type[] params, List<String> passes);

    interface Bi<R2, R, C> extends TypeBehaviorGetter<R, C> {
        R2 getSecondary(@Nullable Field field, Class<C> cls, Type raw, @Nullable Type[] params, List<String> passes, String key);
    }
}
