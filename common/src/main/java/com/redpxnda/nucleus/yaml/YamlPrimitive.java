package com.redpxnda.nucleus.yaml;

import com.google.gson.internal.LazilyParsedNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class YamlPrimitive extends YamlElement {

    private final Object value;

    /**
     * Create a primitive containing a boolean value.
     *
     * @param bool the value to create the primitive with.
     */
    public YamlPrimitive(Boolean bool) {
        value = Objects.requireNonNull(bool);
    }

    /**
     * Create a primitive containing a {@link Number}.
     *
     * @param number the value to create the primitive with.
     */
    public YamlPrimitive(Number number) {
        value = Objects.requireNonNull(number);
    }

    /**
     * Create a primitive containing a String value.
     *
     * @param string the value to create the primitive with.
     */
    public YamlPrimitive(String string) {
        value = Objects.requireNonNull(string);
    }

    /**
     * Create a primitive containing a character. The character is turned into a one character String
     * since Yaml only supports String.
     *
     * @param c the value to create the primitive with.
     */
    public YamlPrimitive(Character c) {
        // convert characters to strings since in Yaml, characters are represented as a single character string
        value = Objects.requireNonNull(c).toString();
    }

    /**
     * Returns the same value as primitives are immutable.
     */
    @Override
    public YamlPrimitive deepCopy() {
        return this;
    }

    /**
     * Check whether this primitive contains a boolean value.
     *
     * @return true if this primitive contains a boolean value, false otherwise.
     */
    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    /**
     * Convenience method to get this element as a boolean value.
     * If this primitive {@linkplain #isBoolean() is not a boolean}, the string value
     * is parsed using {@link Boolean#parseBoolean(String)}. This means {@code "true"} (ignoring
     * case) is considered {@code true} and any other value is considered {@code false}.
     */
    @Override
    public boolean getAsBoolean() {
        if (isBoolean()) {
            return ((Boolean) value).booleanValue();
        }
        // Check to see if the value as a String is "true" in any case.
        return Boolean.parseBoolean(getAsString());
    }

    /**
     * Check whether this primitive contains a Number.
     *
     * @return true if this primitive contains a Number, false otherwise.
     */
    public boolean isNumber() {
        return value instanceof Number;
    }

    /**
     * Convenience method to get this element as a {@link Number}.
     * If this primitive {@linkplain #isString() is a string}, a lazily parsed {@code Number}
     * is constructed which parses the string when any of its methods are called (which can
     * lead to a {@link NumberFormatException}).
     *
     * @throws UnsupportedOperationException if this primitive is neither a number nor a string.
     */
    @Override
    public Number getAsNumber() {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof String) {
            return new LazilyParsedNumber((String) value);
        }
        throw new UnsupportedOperationException("Primitive is neither a number nor a string");
    }

    /**
     * Check whether this primitive contains a String value.
     *
     * @return true if this primitive contains a String value, false otherwise.
     */
    public boolean isString() {
        return value instanceof String;
    }

    // Don't add Javadoc, inherit it from super implementation; no exceptions are thrown here
    @Override
    public String getAsString() {
        if (value instanceof String) {
            return (String) value;
        } else if (isNumber()) {
            return getAsNumber().toString();
        } else if (isBoolean()) {
            return ((Boolean) value).toString();
        }
        throw new AssertionError("Unexpected value type: " + value.getClass());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public double getAsDouble() {
        return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public BigInteger getAsBigInteger() {
        return value instanceof BigInteger ?
                (BigInteger) value : new BigInteger(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public float getAsFloat() {
        return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
    }

    /**
     * Convenience method to get this element as a primitive long.
     *
     * @return this element as a primitive long.
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public long getAsLong() {
        return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public short getAsShort() {
        return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public int getAsInt() {
        return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public byte getAsByte() {
        return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
    }

    /**
     * @throws UnsupportedOperationException if the string value of this
     * primitive is empty.
     * This method is misleading, as it does not get this element as a char but rather as
     * a string's first character.
     */
    @Deprecated
    @Override
    public char getAsCharacter() {
        String s = getAsString();
        if (s.isEmpty()) {
            throw new UnsupportedOperationException("String value is empty");
        } else {
            return s.charAt(0);
        }
    }

    /**
     * Returns the hash code of this object.
     */
    @Override
    public int hashCode() {
        if (value == null) {
            return 31;
        }
        // Using recommended hashing algorithm from Effective Java for longs and doubles
        if (isIntegral(this)) {
            long value = getAsNumber().longValue();
            return (int) (value ^ (value >>> 32));
        }
        if (value instanceof Number) {
            long value = Double.doubleToLongBits(getAsNumber().doubleValue());
            return (int) (value ^ (value >>> 32));
        }
        return value.hashCode();
    }

    /**
     * Returns whether the other object is equal to this. This method only considers
     * the other object to be equal if it is an instance of {@code YamlPrimitive} and
     * has an equal value.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        YamlPrimitive other = (YamlPrimitive)obj;
        if (value == null) {
            return other.value == null;
        }
        if (isIntegral(this) && isIntegral(other)) {
            return getAsNumber().longValue() == other.getAsNumber().longValue();
        }
        if (value instanceof Number && other.value instanceof Number) {
            double a = getAsNumber().doubleValue();
            // Java standard types other than double return true for two NaN. So, need
            // special handling for double.
            double b = other.getAsNumber().doubleValue();
            return a == b || (Double.isNaN(a) && Double.isNaN(b));
        }
        return value.equals(other.value);
    }

    /**
     * Returns true if the specified number is an integral type
     * (Long, Integer, Short, Byte, BigInteger)
     */
    private static boolean isIntegral(YamlPrimitive primitive) {
        if (primitive.value instanceof Number number) {
            return number instanceof BigInteger || number instanceof Long || number instanceof Integer
                    || number instanceof Short || number instanceof Byte;
        }
        return false;
    }
}
