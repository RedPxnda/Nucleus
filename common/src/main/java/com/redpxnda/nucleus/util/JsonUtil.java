package com.redpxnda.nucleus.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;

import java.util.function.Consumer;
import java.util.function.Function;

public class JsonUtil extends JsonHelper {
    public static void runIfPresent(JsonObject object, String key, Consumer<JsonElement> ifPresent) {
        JsonElement element = object.get(key);
        if (element != null)
            ifPresent.accept(element);
    }
    public static <T> T getIfPresent(JsonObject object, String key, Function<JsonElement, T> ifPresent, T ifFailed) {
        JsonElement element = object.get(key);
        return element == null ? ifFailed : ifPresent.apply(element);
    }
    public static <T> T getIfObject(JsonElement element, Function<JsonObject, T> func, T ifFailed) {
        return element instanceof JsonObject object ? func.apply(object) : ifFailed;
    }

    public static JsonElement getOrElse(JsonObject object, String key, JsonElement ifFailed) {
        JsonElement element = object.get(key);
        return element == null ? ifFailed : element;
    }
    public static double getOrElse(JsonObject object, String key, double ifFailed) {
        JsonElement element = object.get(key);
        return element == null ? ifFailed : element.getAsDouble();
    }
    public static float getOrElse(JsonObject object, String key, float ifFailed) {
        JsonElement element = object.get(key);
        return element == null ? ifFailed : element.getAsFloat();
    }
    public static int getOrElse(JsonObject object, String key, int ifFailed) {
        JsonElement element = object.get(key);
        return element == null ? ifFailed : element.getAsInt();
    }
    public static String getOrElse(JsonObject object, String key, String ifFailed) {
        JsonElement element = object.get(key);
        return element == null ? ifFailed : element.getAsString();
    }
}
