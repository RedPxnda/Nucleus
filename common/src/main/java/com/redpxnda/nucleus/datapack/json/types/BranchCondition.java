package com.redpxnda.nucleus.datapack.json.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BranchCondition<T> {
    public static <T> Codec<BranchCondition<T>> codec(Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Evaluable.Codecs.STRING_OR_VALUE.fieldOf("if").forGetter(c -> c.condition),
                codec.fieldOf("then").forGetter(c -> c.primary),
                codec.fieldOf("otherwise").forGetter(c -> c.secondary)
        ).apply(instance, BranchCondition::new));
    }

    public final Evaluable condition;
    public final T primary;
    public final T secondary;

    public BranchCondition(Evaluable condition, T primary, T secondary) {
        this.condition = condition;
        this.primary = primary;
        this.secondary = secondary;
    }
}
