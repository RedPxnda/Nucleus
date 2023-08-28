package com.redpxnda.nucleus.wrappers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WrapperMethod {
    String[] alias() default {};
    String removePrefix() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @interface AllInClass {}
}
