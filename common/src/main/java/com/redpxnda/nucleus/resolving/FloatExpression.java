package com.redpxnda.nucleus.resolving;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.ResolverCodec;

import java.util.function.Function;

public class FloatExpression extends ExpressionResolver<Float> {
    public static final Codec<FloatExpression> codec = new ResolverCodec<>(Resolver::forFloat, Codec.FLOAT);

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
