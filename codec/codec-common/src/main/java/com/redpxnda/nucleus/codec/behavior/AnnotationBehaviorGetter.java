package com.redpxnda.nucleus.codec.behavior;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface AnnotationBehaviorGetter<A extends Annotation, R> {
    R get(A annotation, Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot);

    interface Bi<A extends Annotation, R, R2> extends AnnotationBehaviorGetter<A, R> {
        R2 getSecondary(A annotation, Field field, Class cls, Type raw, @Nullable Type[] params, boolean isRoot, String key);
    }
}
