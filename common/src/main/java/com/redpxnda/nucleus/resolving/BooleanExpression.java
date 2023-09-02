package com.redpxnda.nucleus.resolving;

import java.util.function.Function;

public class BooleanExpression extends ExpressionResolver<Boolean> {
    public BooleanExpression(String base) {
        super(Boolean.class, base);
    }

    public BooleanExpression(String base, Function<String, String> regex, int varGroup) {
        super(Boolean.class, base, regex, varGroup);
    }

    @Override
    protected Boolean calculate() {
        return getValue().getBooleanValue();
    }
}
