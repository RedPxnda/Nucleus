package com.redpxnda.nucleus.resolving;

import java.util.function.Function;

public class FloatExpression extends ExpressionResolver<Float> {
    public FloatExpression(String base) {
        super(Float.class, base);
    }

    public FloatExpression(String base, Function<String, String> regex, int varGroup) {
        super(Float.class, base, regex, varGroup);
    }

    @Override
    protected Float calculate() {
        return getValue().getNumberValue().floatValue();
    }
}
