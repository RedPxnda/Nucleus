package com.redpxnda.nucleus.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsoncNull extends JsoncElement {
    public static void main(String[] args) {
        String json = """
                {
                    unquotedKey: "value!"
                }
                """;

        System.out.println(new Gson().fromJson(json, JsonObject.class));
    }

    public static final JsoncNull INSTANCE = new JsoncNull();

    private JsoncNull() {}

    @Override
    public JsoncElement copy() {
        return INSTANCE;
    }

    @Override
    public String toString(int depth) {
        return "null";
    }
}
