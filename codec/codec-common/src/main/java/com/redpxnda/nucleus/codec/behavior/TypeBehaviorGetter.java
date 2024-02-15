package com.redpxnda.nucleus.codec.behavior;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface TypeBehaviorGetter<R, C> {
    R get(@Nullable Field field, Class<C> cls, Type raw, @Nullable Type[] params, boolean isRoot);

    interface Bi<R2, R, C> extends TypeBehaviorGetter<R, C> {
        R2 getSecondary(@Nullable Field field, Class<C> cls, Type raw, @Nullable Type[] params, boolean isRoot, String key);
    }
}
