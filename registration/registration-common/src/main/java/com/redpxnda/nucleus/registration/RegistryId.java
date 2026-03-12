package com.redpxnda.nucleus.registration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryId {
    /**
     * @return the path of this identifier
     */
    String value();

    /**
     * Overrides the registry you want to register this object to.
     * @return the name a static field in the same class as this field that is a Supplier<Registry> that gives the registry you want to register this object to
     */
    String registry() default "default";
}
