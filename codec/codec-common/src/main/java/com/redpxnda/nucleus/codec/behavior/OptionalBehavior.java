package com.redpxnda.nucleus.codec.behavior;

/**
 * Behavior outlining what to do when a field is optional. See {@link CodecBehavior.Optional}
 */
public enum OptionalBehavior {
    /**
     * The field will be set to null
     */
    NULL,

    /**
     * The field will be the default value, defined in the object
     */
    DEFAULT,

    /**
     * The field cannot be optional (error thrown)
     */
    DISALLOW
}
