package com.redpxnda.nucleus.resolving;

import java.util.function.Function;

public class DoubleExpression extends ExpressionResolver<Double> {
    public DoubleExpression(String base) {
        super(Double.class, base);
    }

    public DoubleExpression(String base, Function<String, String> regex, int varGroup) {
        super(Double.class, base, regex, varGroup);
    }

    @Override
    protected Double calculate() {
        return getValue().getNumberValue().doubleValue();
    }
}
