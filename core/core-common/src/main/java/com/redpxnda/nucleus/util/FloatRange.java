package com.redpxnda.nucleus.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A marker to say that this float is within a specific range... used for AutoCodec(Codec module)
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
