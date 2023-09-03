package com.redpxnda.nucleus.resolving;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.ResolverCodec;

import java.util.function.Function;

public class BooleanExpression extends ExpressionResolver<Boolean> {
    public static final Codec<BooleanExpression> codec = new ResolverCodec<>(Resolver::forBoolean, Codec.BOOL);

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
