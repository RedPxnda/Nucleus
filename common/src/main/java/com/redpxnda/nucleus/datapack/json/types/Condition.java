package com.redpxnda.nucleus.datapack.json.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Condition<T> {
    public static <T> Codec<Condition<T>> codec(Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Evaluable.Codecs.STRING_OR_VALUE.optionalFieldOf("if", new Evaluable("true")).forGetter(c -> c.condition),
                codec.fieldOf("then").forGetter(c -> c.result)
        ).apply(instance, Condition::new));
    }

    public final Evaluable condition;
    public final T result;

    public Condition(Evaluable condition, T primary) {
        this.condition = condition;
        this.result = primary;
    }
}
