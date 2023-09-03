package com.redpxnda.nucleus.resolving;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.codec.ResolverCodec;

import java.util.function.Function;

@AutoCodec.Override("codec")
public class StringResolver extends Resolver<String> {
    public static final Codec<StringResolver> codec = new ResolverCodec<>(Resolver::forString, Codec.STRING);

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
