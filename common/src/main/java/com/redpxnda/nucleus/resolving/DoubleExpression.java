package com.redpxnda.nucleus.resolving;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.ResolverCodec;

import java.util.function.Function;

public class DoubleExpression extends ExpressionResolver<Double> {
    public static final Codec<DoubleExpression> codec = new ResolverCodec<>(Resolver::forDouble, Codec.DOUBLE);

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
