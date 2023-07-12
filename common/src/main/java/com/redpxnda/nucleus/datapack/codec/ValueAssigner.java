package com.redpxnda.nucleus.datapack.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

/**
 * {@link ValueAssigner}s are great tools to (de)serialize modifications to specific objects.
 * If I had an object that I want to allow a series of modifications for within JSON, {@link ValueAssigner}s
 * are how I would do that. Use the {@link ValueAssigner.Builder} to create a Codec for a {@link ValueAssigner}.
 * <p>
 * Example value assigner (serialized):
 * {
 *   "value1": "cool String!",
 *   "value2": 16,
 *   "value3": true
 * }
 * In this example, the "value1" section will apply the {@link BiConsumer} saved under the "value1" key,
 * inputting a value of "cool String!" for the second input. The first input will be the instance of the
 * object you define in {@link ValueAssigner#assignTo(T)}.
 *
 * @param <T> The type of object this ValueAssigner assigns to.
 */
public class ValueAssigner<T> {
    private final Map<String, Instruction<T, ?>> instructions;

    public ValueAssigner(Map<String, Instruction<T, ?>> instructions) {
        this.instructions = instructions;
    }

    public void assignTo(T instance) {
        instructions.forEach((k, instruction) -> instruction.assigner().accept(instance));
    }

    public static class Builder<T> {
        private final Map<String, Template<T, ?>> templates = new HashMap<>();

        public <A> Builder<T> add(String key, Codec<A> codec, BiConsumer<T, A> assigner, A defInput) {
            templates.put(key, new Template<>(codec, assigner, defInput));
            return this;
        }

        public Codec<ValueAssigner<T>> codec() {
            return new VACodec<>(templates);
        }
    }

    public record Template<T, A> (Codec<A> codec, BiConsumer<T, A> assigner, A defInput) {}
    public record Instruction<T, A> (Codec<A> codec, Consumer<T> assigner, Object input) {}

    public static class VACodec<T> implements Codec<ValueAssigner<T>> {
        private final Map<String, Template<T, ?>> map;

        public VACodec(Map<String, Template<T, ?>> map) {
            this.map = map;
        }

        @Override
        public <A> DataResult<Pair<ValueAssigner<T>, A>> decode(DynamicOps<A> ops, A input) {
            Map<String, Instruction<T, ?>> inst = new HashMap<>();
            map.forEach((key, t) -> {
                Optional<A> val = ops.get(input, key).result();
                if (val.isPresent()) {
                    Object obj = t.codec().parse(ops, val.get()).getOrThrow(false, s -> LOGGER.error("Failed to deserialize key '{}' during ValueAssigner creation! -> {}", key, s));
                    inst.put(key, new Instruction<>(t.codec(), subject -> ((BiConsumer<T, Object>) t.assigner()).accept(subject, obj), obj));
                } else {
                    inst.put(key, new Instruction<>(t.codec(), subject -> ((BiConsumer<T, Object>) t.assigner()).accept(subject, t.defInput), t.defInput));
                }
            });
            return DataResult.success(Pair.of(new ValueAssigner<>(inst), input));
        }

        @Override
        public <A> DataResult<A> encode(ValueAssigner<T> input, DynamicOps<A> ops, A prefix) {
            Map<A, A> map = new HashMap<>();
            input.instructions.forEach((key, instruction) -> {
                DataResult<A> obj = ((Codec<Object>) instruction.codec()).encodeStart(ops, instruction.input());
                map.put(ops.createString(key), obj.getOrThrow(false, s -> LOGGER.error("Failed to encode key '{}' for ValueAssigner! -> {}", key, s)));
            });
            return DataResult.success(ops.createMap(map));
        }
    }
}
