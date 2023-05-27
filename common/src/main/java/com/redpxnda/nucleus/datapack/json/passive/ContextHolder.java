package com.redpxnda.nucleus.datapack.json.passive;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ContextHolder {
    private final Map<String, Operation<?, ?>> operations = new HashMap<>();

    public ContextHolder(JsonObject object) {
        deserializeJson(object);
    }

    private void deserializeJson(JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getValue() instanceof JsonPrimitive primitive && primitive.isString())
                operations.put(entry.getKey(), new Operation<>(primitive.getAsString()));
            else if (entry.getValue() instanceof JsonObject obj) {
                String root = null;
                if (obj.has("from"))
                    root = obj.get("from").getAsString();
                if (!obj.has("operation"))
                    throw new IllegalArgumentException("Failed to parse OperationContext for a ContextHolder: Missing \"operation\" value.");
                String operation = obj.get("operation").getAsString();
                if (root == null) {
                    operations.put(entry.getKey(), new Operation<>(operation));
                    continue;
                }
                operations.put(entry.getKey(), new Operation<>(operations.get(root), operation, obj));
            }
        }
    }

    public Map<String, Operation<?, ?>> getOperations() {
        return operations;
    }

    public Map<String, Object> resolve(RootContext ref) {
        return operations.entrySet().stream()
                .map(entry -> {
                    Object obj = null;
                    try {
                        obj = entry.getValue().call(ref);
                    } catch (EvaluationException | ParseException e) {
                        e.printStackTrace();
                    }
                    ref.insert(entry.getKey(), obj);
                    return Pair.of(entry.getKey(), obj);
                })
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }
}
