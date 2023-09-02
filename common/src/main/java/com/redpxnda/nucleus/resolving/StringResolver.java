package com.redpxnda.nucleus.resolving;

import java.util.function.Function;

public class StringResolver extends Resolver<String> {
    public StringResolver(String base) {
        super(String.class, base);
    }

    public StringResolver(String base, Function<String, String> regex, int varGroup) {
        super(String.class, base, regex, varGroup);
    }

    @Override
    public String calculate() {
        return resolved;
    }
}
