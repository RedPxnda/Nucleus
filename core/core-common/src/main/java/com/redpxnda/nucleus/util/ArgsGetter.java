package com.redpxnda.nucleus.util;

/**
 * An interface used to help ease the getting of elements from generic object arrays.
 * Using the get method can often throw a ClassCastException, so make sure to use it correctly. (aka when you're sure of the type of the arg)
 */
public interface ArgsGetter {
    static ArgsGetter of(Object[] args) {
        return new Impl(args);
    }
    class Impl implements ArgsGetter {
        private final Object[] args;

        public Impl(Object[] args) {
            this.args = args;
        }

        @Override
        public <T> T get(int index) {
            return (T) args[index];
        }
    }

    <T> T get(int index);
}
