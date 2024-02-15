package com.redpxnda.nucleus.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A marker to say that this integer is within a specific range... used for AutoCodec(Codec module)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegerRange {
    /**
     * The minimum value in this range, inclusive
     */
    int min() default Integer.MIN_VALUE;

    /**
     * The maximum value in this range, inclusive
     */
    int max() default Integer.MAX_VALUE;

    /**
     * If true, numbers outside the range will cause a crash. Otherwise, they will be ignored and set to the respective bound of the range.
     */
    boolean failHard() default false;
}
