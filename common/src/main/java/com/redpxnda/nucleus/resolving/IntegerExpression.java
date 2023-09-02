package com.redpxnda.nucleus.resolving;

import java.util.function.Function;

public class IntegerExpression extends ExpressionResolver<Integer> {
    public IntegerExpression(String base) {
        super(Integer.class, base);
    }

    public IntegerExpression(String base, Function<String, String> regex, int varGroup) {
        super(Integer.class, base, regex, varGroup);
    }

    @Override
    protected Integer calculate() {
        return getValue().getNumberValue().intValue();
    }
}
