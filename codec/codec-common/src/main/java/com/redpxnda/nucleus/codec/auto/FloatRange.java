package com.redpxnda.nucleus.codec.auto;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A marker to tell the {@link AutoCodec} that this float should use a special float range codec
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FloatRange {
    /**
     * The minimum value in this range, inclusive
     */
    float min() default Float.MIN_VALUE;

    /**
     * The maximum value in this range, inclusive
     */
    float max() default Float.MAX_VALUE;

    /**
     * If true, numbers outside the range will cause a crash. Otherwise, they will be ignored and set to the respective bound of the range.
     */
    boolean failHard() default false;
}
