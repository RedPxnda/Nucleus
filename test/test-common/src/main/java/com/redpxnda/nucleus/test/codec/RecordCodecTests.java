package com.redpxnda.nucleus.test.codec;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;

import java.util.Map;

public class RecordCodecTests {
    public static void runAutoCodecTests() {
        // ------------------------------------------------------------
        // Test full decode: all fields present
        // ------------------------------------------------------------
        JsonObject fullObject = new JsonObject();
        fullObject.addProperty("test", "string");
        fullObject.addProperty("number", 10);
        fullObject.addProperty("opt_string", "hello");
        fullObject.addProperty("opt_int", 42);

        var fullResult = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, fullObject)
                .result();

        if (fullResult.isEmpty()) {
            Nucleus.getLogger().error("x full record decode failed");
            return;
        }

        TestRecord full = fullResult.get().getFirst();

        if (!"string".equals(full.testString())) {
            Nucleus.getLogger().error("x failed string field decode");
        }
        if (full.number() != 10) {
            Nucleus.getLogger().error("x failed number field decode");
        }
        if (!"hello".equals(full.optionalString())) {
            Nucleus.getLogger().error("x failed optional string decode");
        }
        if (full.optionalInt() != 42) {
            Nucleus.getLogger().error("x failed optional int decode");
        }

        Nucleus.getLogger().info("sucess full record decode test passed");

        // ------------------------------------------------------------
        // Test missing optional fields (should succeed)
        // ------------------------------------------------------------
        JsonObject missingOptional = new JsonObject();
        missingOptional.addProperty("test", "required");
        missingOptional.addProperty("number", 5);

        var resultOptional = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, missingOptional)
                .result();

        if (resultOptional.isEmpty()) {
            Nucleus.getLogger().error("x decode failed when optional fields were missing");
        } else {
            TestRecord record = resultOptional.get().getFirst();
            if (!"required".equals(record.testString()) || record.number() != 5) {
                Nucleus.getLogger().error("x incorrect values on missing-optional test");
            }
            if (record.optionalString() != null || record.optionalInt() != null) {
                Nucleus.getLogger().error("x optional fields not null when missing");
            } else {
                Nucleus.getLogger().info("sucess missing optional fields handled correctly");
            }
        }

        // ------------------------------------------------------------
        // Test missing required field (should fail)
        // ------------------------------------------------------------
        JsonObject missingRequired = new JsonObject();
        missingRequired.addProperty("number", 10);

        var resultMissingRequired = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, missingRequired)
                .result();

        if (resultMissingRequired.isEmpty()) {
            Nucleus.getLogger().info("sucess missing required field correctly caused failure");
        } else {
            Nucleus.getLogger().error("x missing required field did not cause failure");
        }

        // ------------------------------------------------------------
        // Test renamed JSON field (Name annotation)
        // ------------------------------------------------------------
        JsonObject renamedObject = new JsonObject();
        renamedObject.addProperty("test", "renamed");
        renamedObject.addProperty("number", 99);
        renamedObject.addProperty("testString", "base name");

        var renamedResult = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, renamedObject)
                .result();

        if (renamedResult.isEmpty()) {
            Nucleus.getLogger().error("x decode failed for renamed JSON fields");
        } else {
            TestRecord record = renamedResult.get().getFirst();
            if (!"renamed".equals(record.testString()) || record.number() != 99) {
                Nucleus.getLogger().error("x renamed JSON fields decoded incorrectly");
            } else {
                Nucleus.getLogger().info("sucess renamed JSON fields handled correctly via @CodecBehaviour.Name");
            }
        }

        // ------------------------------------------------------------
        // Test null optional field (should succeed)
        // ------------------------------------------------------------
        JsonObject nullOptional = new JsonObject();
        nullOptional.addProperty("test", "nullable");
        nullOptional.addProperty("number", 1);
        nullOptional.add("opt_string", JsonNull.INSTANCE);

        var nullResult = AutoCodec.of(TestRecord.class).codec()
                .decode(JsonOps.INSTANCE, nullOptional)
                .result();

        if (nullResult.isEmpty()) {
            Nucleus.getLogger().error("x decode failed when optional field explicitly null");
        } else {
            TestRecord record = nullResult.get().getFirst();
            if (record.optionalString() != null) {
                Nucleus.getLogger().error("x optional field not null when explicitly null");
            } else {
                Nucleus.getLogger().info("sucess optional field correctly handled when explicitly null");
            }
        }

        AutoCodec<TestRecord> codecWithDefaults = AutoCodec.of(TestRecord.class)
                .setRecordDefaults(Map.of(
                        "opt_string", () -> "default_string",
                        "opt_int", () -> 123
                ));

        // JSON missing optional fields
        JsonObject missingFields = new JsonObject();
        missingFields.addProperty("test", "required");
        missingFields.addProperty("number", 5);

        var defaultResult = codecWithDefaults.codec()
                .decode(JsonOps.INSTANCE, missingFields)
                .result();

        if (defaultResult.isEmpty()) {
            Nucleus.getLogger().error("x decode failed when defaults should be applied");
        } else {
            TestRecord record = defaultResult.get().getFirst();
            if (!"required".equals(record.testString()) || record.number() != 5) {
                Nucleus.getLogger().error("x wrong values for required fields with defaults");
            }
            if (!"default_string".equals(record.optionalString())) {
                Nucleus.getLogger().error("x default not applied to optionalString");
            }
            if (!Integer.valueOf(123).equals(record.optionalInt())) {
                Nucleus.getLogger().error("x default not applied to optionalInt");
            } else {
                Nucleus.getLogger().info("sucess recordDefaultGetter applied correctly to missing fields");
            }
        }

        // JSON with explicit null for optional fields (should still use defaults)
        JsonObject nullFields = new JsonObject();
        nullFields.addProperty("test", "required2");
        nullFields.addProperty("number", 6);
        nullFields.add("opt_string", JsonNull.INSTANCE);
        nullFields.add("opt_int", JsonNull.INSTANCE);

        var nullDefaultResult = codecWithDefaults.codec()
                .decode(JsonOps.INSTANCE, nullFields)
                .result();

        if (nullDefaultResult.isEmpty()) {
            Nucleus.getLogger().error("x decode failed for null fields with defaults");
        } else {
            TestRecord record = nullDefaultResult.get().getFirst();
            if (!"default_string".equals(record.optionalString())) {
                Nucleus.getLogger().error("x default not applied to optionalString when null");
            }
            if (!Integer.valueOf(123).equals(record.optionalInt())) {
                Nucleus.getLogger().error("x default not applied to optionalInt when null");
            } else {
                Nucleus.getLogger().info("sucess recordDefaultGetter applied correctly to null fields");
            }
        }
    }

    public record TestRecord(
            @AutoCodec.Name("test") String testString,
            @AutoCodec.Name("number") int number,
            @CodecBehavior.Optional @AutoCodec.Name("opt_string") String optionalString,
            @CodecBehavior.Optional @AutoCodec.Name("opt_int") Integer optionalInt
    ) {
    }
}
