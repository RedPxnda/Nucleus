package com.redpxnda.nucleus.util.json;

import java.util.Objects;

public class JsoncPrimitive extends JsoncElement {
    private final Object value;

    public JsoncPrimitive(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean asBoolean() {
        if (isBoolean()) {
            return (Boolean) value;
        }
        // Check to see if the value as a String is "true" in any case.
        throw new UnsupportedOperationException("Primitive is not a boolean!");
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public Number getAsNumber() {
        if (isNumber())
            return (Number) value;
        throw new UnsupportedOperationException("Primitive is not a number!");
    }

    public boolean isString() {
        return value instanceof String;
    }

    public String getAsString() {
        return value.toString();
    }

    @Override
    public JsoncElement copy() {
        return this;
    }

    @Override
    public String toString(int depth) {
        if (isString()) return '"' + getAsString() + '"';
        return getAsString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsoncPrimitive that = (JsoncPrimitive) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
