package com.redpxnda.nucleus.yaml;

public class YamlNull extends YamlElement {
    public static final YamlNull INSTANCE = new YamlNull();

    private YamlNull() {}

    @Override
    public YamlElement deepCopy() {
        return this;
    }

    /**
     * All instances of {@code YamlNull} have the same hash code since they are indistinguishable.
     */
    @Override
    public int hashCode() {
        return YamlNull.class.hashCode();
    }

    /**
     * All instances of {@code YamlNull} are considered equal.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof YamlNull;
    }
}
