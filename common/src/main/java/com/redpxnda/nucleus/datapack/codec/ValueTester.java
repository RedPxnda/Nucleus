package com.redpxnda.nucleus.datapack.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

/**
 * {@link ValueTester}s are great tools to (de)serialize checks for specific objects.
 * If I had an object with several fields that I want to compare to other values, {@link ValueTester}s
 * are how I would do that. Use the {@link ValueTester.Builder} to create a Codec for a {@link ValueTester}.
 * <p>
 * Example value assigner (serialized):
 * {
 *   "value1": "cool String!",
 *   "value2": 16,
 *   "value3": true
 * }
 * In this example, the field "value1" will be tested against the string "cool String!" when using
 * the {@link ValueTester#test(Object)} method. Similar idea with the other fields.
 * If and only if every value/field matches every set input, {@link ValueTester#test(Object)} will
 * return true.
 *
 * @param <T> The type of object this ValueTester tests for.
 */
public class ValueTester<T> {
    private final Map<String, Instruction<T, ?>> instructions;

    public ValueTester(Map<String, Instruction<T, ?>> instructions) {
        this.instructions = instructions;
    }

    public boolean test(T instance) {
        for (Map.Entry<String, Instruction<T, ?>> entry : instructions.entrySet()) {
            Instruction<T, ?> instruction = entry.getValue();
            if (!instruction.field.apply(instance, instruction.input))
                return false;
        }
        return true;
    }

    public static class Builder<T> {
        private final Map<String, Template<T, ?>> templates = new HashMap<>();

        public <A> Builder<T> add(String key, Codec<A> codec, BiFunction<T, A, Boolean> getter, A defInput) {
            templates.put(key, new Template<>(codec, (BiFunction<T, Object, Boolean>) getter, defInput));
            return this;
        }

        public Codec<ValueTester<T>> codec() {
            return new VTCodec<>(templates);
        }
    }

    public record Template<T, A> (Codec<A> codec, BiFunction<T, Object, Boolean> field, A defInput) {}
    public record Instruction<T, A> (Codec<A> codec, BiFunction<T, Object, Boolean> field, Object input) {}

    public static class VTCodec<T> implements Codec<ValueTester<T>> {
        private final Map<String, Template<T, ?>> map;

        public VTCodec(Map<String, Template<T, ?>> map) {
            this.map = map;
        }

        @Override
        public <A> DataResult<Pair<ValueTester<T>, A>> decode(DynamicOps<A> ops, A input) {
            Map<String, Instruction<T, ?>> inst = new HashMap<>();
            map.forEach((key, t) -> {
                Optional<A> val = ops.get(input, key).result();
                if (val.isPresent()) {
                    Object obj = t.codec().parse(ops, val.get()).getOrThrow(false, s -> LOGGER.error("Failed to deserialize key '{}' during ValueTester creation! -> {}", key, s));
                    inst.put(key, new Instruction<>(t.codec(), t.field, obj));
                } else {
                    inst.put(key, new Instruction<>(t.codec(), t.field, t.defInput));
                }
            });
            return DataResult.success(Pair.of(new ValueTester<>(inst), input));
        }

        @Override
        public <A> DataResult<A> encode(ValueTester<T> input, DynamicOps<A> ops, A prefix) {
            Map<A, A> map = new HashMap<>();
            input.instructions.forEach((key, instruction) -> {
                DataResult<A> obj = ((Codec<Object>) instruction.codec()).encodeStart(ops, instruction.input());
                map.put(ops.createString(key), obj.getOrThrow(false, s -> LOGGER.error("Failed to encode key '{}' for ValueTester! -> {}", key, s)));
            });
            return DataResult.success(ops.createMap(map));
        }
    }
/*    public static void main(String[] args) {
        final String json = """
                {
                    "anInt": 52,
                    "aString": "!!",
                    "anotherString": "it do be working?"
                }
                """;
        final Test test = new Test(52, "woah !!", "it do be working?");

        Codec<ValueTester<Test>> tester = new ValueTester.Builder<Test>()
                .add("anInt", Codec.INT, (t, i) -> t.anInt == i, 0)
                .add("aString", Codec.STRING, (t, s) -> t.aString.contains(s), "nah")
                .add("anotherString", Codec.STRING, (t, s) -> t.anotherString.equals(s), "po")
                .codec();

        var res = tester.decode(JsonOps.INSTANCE, new Gson().fromJson(json, JsonObject.class));
        System.out.println(res.getOrThrow(false, s -> {}).getFirst().test(test));
    }

    public static class Test {
        final int anInt;
        final String aString;
        final String anotherString;

        public Test(int anInt, String aString, String anotherString) {
            this.anInt = anInt;
            this.aString = aString;
            this.anotherString = anotherString;
        }
    }*/
}
