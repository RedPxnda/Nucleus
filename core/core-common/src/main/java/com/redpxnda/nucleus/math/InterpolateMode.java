package com.redpxnda.nucleus.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.redpxnda.nucleus.util.json.JsonUtil;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ExtraCodecs;

public interface InterpolateMode {
    InterpolateMode NONE = (delta, last, current) -> current;
    InterpolateMode LERP = MathUtil::lerp;
    InterpolateMode COS = (delta, last, current) -> {
        double d = (1 - Math.cos(delta * Math.PI)) / 2;
        return MathUtil.lerp(d, last, current);
    };

    Map<String, Creator> interpolateModes = new HashMap<>();
    Codec<InterpolateMode> codec = ExtraCodecs.JSON.flatComapMap(
            element -> {
                String key;
                if (element instanceof JsonPrimitive primitive)
                    key = primitive.getAsString();
                else if (element instanceof JsonObject object)
                    key = object.getAsJsonPrimitive("type").getAsString();
                else
                    throw new JsonParseException("Failed to get key 'type' from JSON! -> Not a JSON object: " + "'" + element + "'");
                Creator creator = interpolateModes.get(key);
                if (creator == null) throw new JsonParseException("Could not find interpolate mode '" + key + "'! JSON: " + element);
                return creator.createFrom(element);
            },
            mode -> {
                try {
                    return DataResult.success(mode.toJson());
                } catch (Exception e) {
                    return DataResult.error(() -> "Failed to encode InterpolateMode: " + e.getMessage());
                }
            }
    );


    default JsonElement toJson() {
        if (this == NONE) return new JsonPrimitive("none");
        if (this == LERP) return new JsonPrimitive("lerp");
        if (this == COS) return new JsonPrimitive("cosine");
        throw new UnsupportedOperationException("Encoding not implemented for InterpolateMode: " + this.getClass().getSimpleName());
    }

    static void init() {
        interpolateModes.put("none", e -> InterpolateMode.NONE);
        interpolateModes.put("lerp", e -> LERP);
        interpolateModes.put("cosine", e -> COS);
        interpolateModes.put("easeIn", e -> new EaseIn(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeOut", e -> new EaseOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeInOut", e -> new EaseInOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
    }

    double interpolate(float delta, double last, double current);

    interface Creator {
        InterpolateMode createFrom(JsonElement element);
    }

    record EaseIn(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(Math.pow(delta, amplifier), last, current);
        }

        @Override
        public JsonElement toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "easeIn");
            obj.addProperty("amplifier", amplifier);
            return obj;
        }
    }

    record EaseOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier)), last, current);
        }

        @Override
        public JsonElement toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "easeOut");
            obj.addProperty("amplifier", amplifier);
            return obj;
        }
    }

    record EaseInOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            float flippedDelta = MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier));
            float normalDelta = MathUtil.pow(delta, amplifier);
            float finalDelta = MathUtil.lerp(delta, normalDelta, flippedDelta);
            return MathUtil.lerp(finalDelta, last, current);
        }

        @Override
        public JsonElement toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "easeInOut");
            obj.addProperty("amplifier", amplifier);
            return obj;
        }
    }
}
