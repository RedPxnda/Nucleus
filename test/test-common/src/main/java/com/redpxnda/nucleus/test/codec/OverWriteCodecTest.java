package com.redpxnda.nucleus.test.codec;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.auto.AutoCodecSetupException;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;

public class OverWriteCodecTest {
    // ------------------------------------------------------------
// auto = true
// ------------------------------------------------------------
    @CodecBehavior.Override(auto = true)
    public static class AutoOverrideClass {
        @AutoCodec.Name("value")
        int value;

        public AutoOverrideClass() {
        }
    }

    // ------------------------------------------------------------
// Static Codec override (Integer <-> String mapping)
// ------------------------------------------------------------
    public static class StaticCodecOverrideClass {

        public static Codec<Integer> INT_FROM_STRING = new Codec<>() {
            @Override
            public <T> DataResult<Pair<Integer, T>> decode(DynamicOps<T> ops, T input) {
                return Codec.STRING.decode(ops, input).map(p -> new Pair<>(Integer.parseInt(p.getFirst()), p.getSecond()));
            }

            @Override
            public <T> DataResult<T> encode(Integer input, DynamicOps<T> ops, T prefix) {
                return Codec.STRING.encode(String.valueOf(input), ops, prefix);
            }
        };

        @AutoCodec.Name("value")
        @CodecBehavior.Override("INT_FROM_STRING")
        Integer value;

        public StaticCodecOverrideClass() {
        }
    }

    // ------------------------------------------------------------
// Getter override
// ------------------------------------------------------------
    public static class GetterOverrideClass {

        public static CodecBehavior.Getter<String> CUSTOM_GETTER = (f, cls, raw, params, passes) -> Codec.INT.xmap(i -> "custom", s -> 0);

        @AutoCodec.Name("value")
        @CodecBehavior.Override("CUSTOM_GETTER")
        String value;

        public GetterOverrideClass() {
        }
    }

    // ------------------------------------------------------------
// Invalid: non-static field
// ------------------------------------------------------------
    public static class InvalidNonStaticOverrideClass {

        public Codec<String> NOT_STATIC = Codec.STRING;

        @AutoCodec.Name("value")
        @CodecBehavior.Override("NOT_STATIC")
        String value;

        public InvalidNonStaticOverrideClass() {
        }
    }

    // ------------------------------------------------------------
// Invalid: missing field
// ------------------------------------------------------------
    public static class MissingFieldOverrideClass {

        @AutoCodec.Name("value")
        @CodecBehavior.Override("DOES_NOT_EXIST")
        String value;

        public MissingFieldOverrideClass() {
        }
    }

    // ------------------------------------------------------------
// Invalid: wrong type
// ------------------------------------------------------------
    public static class WrongTypeOverrideClass {

        public static String INVALID = "not a codec";

        @AutoCodec.Name("value")
        @CodecBehavior.Override("INVALID")
        String value;

        public WrongTypeOverrideClass() {
        }
    }

    // ------------------------------------------------------------
// NUMBER-like flexible codec (number → string)
// ------------------------------------------------------------
    public static class NumberStringOverrideClass {

        public static Codec<String> NUMBERSTRING = new Codec<>() {
            @Override
            public <T> DataResult<Pair<String, T>> decode(DynamicOps<T> ops, T input) {
                var num = ops.getNumberValue(input);
                if (num.isSuccess()) {
                    return DataResult.success(new Pair<>(num.getOrThrow().toString(), input));
                }
                return Codec.STRING.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(String input, DynamicOps<T> ops, T prefix) {
                return Codec.STRING.encode(input, ops, prefix);
            }
        };

        @AutoCodec.Name("value")
        @CodecBehavior.Override("NUMBERSTRING")
        String value;

        public NumberStringOverrideClass() {
        }
    }

    public static void runOverrideTests() {

        // ------------------------------------------------------------
        // auto = true
        // ------------------------------------------------------------
        JsonObject autoObj = new JsonObject();
        autoObj.addProperty("value", 5);

        var autoResult = AutoCodec.of(AutoOverrideClass.class)
                .codec()
                .decode(JsonOps.INSTANCE, autoObj)
                .result();

        if (autoResult.isEmpty()) {
            Nucleus.getLogger().error("x auto override failed");
        } else if (autoResult.get().getFirst().value != 5) {
            Nucleus.getLogger().error("x auto override wrong value");
        } else {
            Nucleus.getLogger().info("success auto override");
        }

        // ------------------------------------------------------------
        // static codec override
        // ------------------------------------------------------------
        JsonObject staticObj = new JsonObject();
        staticObj.addProperty("value", "123");

        var staticResult = AutoCodec.of(StaticCodecOverrideClass.class)
                .codec()
                .decode(JsonOps.INSTANCE, staticObj)
                .result();

        if (staticResult.isEmpty()) {
            Nucleus.getLogger().error("x static codec override failed");
        } else if (!Integer.valueOf(123).equals(staticResult.get().getFirst().value)) {
            Nucleus.getLogger().error("x static codec produced wrong value");
        } else {
            Nucleus.getLogger().info("success static codec override");
        }

        // ------------------------------------------------------------
        // getter override
        // ------------------------------------------------------------
        /*
        JsonObject getterObj = new JsonObject();
        getterObj.addProperty("value", "ignored");

        var getterResult = AutoCodec.of(GetterOverrideClass.class)
                .codec()
                .decode(JsonOps.INSTANCE, getterObj)
                .result();

        if (getterResult.isEmpty()) {
            Nucleus.getLogger().error("x getter override failed");
        } else if (!"injected_value".equals(getterResult.get().getFirst().value)) {
            Nucleus.getLogger().error("x getter override incorrect");
        } else {
            Nucleus.getLogger().info("success getter override");
        }
         */

        // ------------------------------------------------------------
        // non-static field (should fail)
        // ------------------------------------------------------------
        JsonObject nonStaticObj = new JsonObject();
        nonStaticObj.addProperty("value", "test");

        try {
            AutoCodec.of(InvalidNonStaticOverrideClass.class)
                    .codec()
                    .decode(JsonOps.INSTANCE, nonStaticObj)
                    .result();
            Nucleus.getLogger().error("x non-static override should fail");
        } catch (AutoCodecSetupException ignore) {
            Nucleus.getLogger().info("success non-static override rejected");
        }

        // ------------------------------------------------------------
        // missing field (should fail)
        // ------------------------------------------------------------
        JsonObject missingObj = new JsonObject();
        missingObj.addProperty("value", "test");

        try {
            var missingResult = AutoCodec.of(MissingFieldOverrideClass.class)
                    .codec()
                    .decode(JsonOps.INSTANCE, missingObj)
                    .result();
            Nucleus.getLogger().error("x missing override should fail");
        } catch (AutoCodecSetupException ignore) {
            Nucleus.getLogger().info("success missing override rejected");
        }

        // ------------------------------------------------------------
        // wrong type (should fail)
        // ------------------------------------------------------------
        JsonObject wrongTypeObj = new JsonObject();
        wrongTypeObj.addProperty("value", "test");
        try {
        var wrongTypeResult = AutoCodec.of(WrongTypeOverrideClass.class)
                .codec()
                .decode(JsonOps.INSTANCE, wrongTypeObj)
                .result();
            Nucleus.getLogger().error("x wrong type override should fail");
        } catch (AutoCodecSetupException ignore) {
            Nucleus.getLogger().info("success wrong type override rejected");
        }

        // ------------------------------------------------------------
        // number → string codec
        // ------------------------------------------------------------
        JsonObject numObj = new JsonObject();
        numObj.addProperty("value", 42);

        var numResult = AutoCodec.of(NumberStringOverrideClass.class)
                .codec()
                .decode(JsonOps.INSTANCE, numObj)
                .result();

        if (numResult.isEmpty()) {
            Nucleus.getLogger().error("x number-string override failed");
        } else if (!"42".equals(numResult.get().getFirst().value)) {
            Nucleus.getLogger().error("x number not converted to string");
        } else {
            Nucleus.getLogger().info("success number-string override");
        }
    }
}
