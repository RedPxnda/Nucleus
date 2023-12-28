package com.redpxnda.nucleus.codec.ops;

public class JanksonOps {}/* implements DynamicOps<JsonElement> {
    public static final JanksonOps INSTANCE = new JanksonOps();

    protected JanksonOps() {}

    @Override
    public JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsonElement input) {
        if (input instanceof JsonObject)
            return convertMap(outOps, input);
        if (input instanceof JsonArray)
            return convertList(outOps, input);
        if (input instanceof JsonNull)
            return outOps.empty();
        JsonPrimitive primitive = (JsonPrimitive) input;
        if (primitive.getValue() instanceof String string)
            return outOps.createString(string);
        if (primitive.getValue() instanceof Boolean bl)
            return outOps.createBoolean(bl);
        if (primitive.getValue() instanceof Number num)
            return outOps.createNumeric(num);
        return null;
    }

    @Override
    public DataResult<Number> getNumberValue(JsonElement input) {
        if (input instanceof JsonPrimitive primitive && primitive.getValue() instanceof Number num)
            return DataResult.success(num);
        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public JsonElement createNumeric(Number i) {
        return new JsonPrimitive(i);
    }

    @Override
    public DataResult<String> getStringValue(JsonElement input) {
        if (input instanceof JsonPrimitive primitive && primitive.getValue() instanceof String str)
            return DataResult.success(str);
        return DataResult.error(() -> "Not a string: " + input);
    }

    @Override
    public JsonElement createString(String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, JsonElement value) {
        if (!(list instanceof JsonArray array))
            return DataResult.error(() -> "mergeToList called with non-list: " + list, list);

        JsonArray newArray = array.clone();
        newArray.add(value);
        return DataResult.success(newArray);
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, List<JsonElement> values) {
        if (!(list instanceof JsonArray array))
            return DataResult.error(() -> "mergeToList called with non-list: " + list, list);

        JsonArray newArray = array.clone();
        newArray.addAll(values);
        return DataResult.success(newArray);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, JsonElement key, JsonElement value) {
        if (!(map instanceof JsonObject obj))
            return DataResult.error(() -> "mergeToMap called with non-map: " + map, map);
        if (!(key instanceof JsonPrimitive prim) || !(prim.getValue() instanceof String))
            return DataResult.error(() -> "mergeToMap called with non-string as key: " + key, map);

        JsonObject newObj = obj.clone();
        newObj.put(prim.asString(), value);
        return DataResult.success(newObj);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, MapLike<JsonElement> values) {
        if (!(map instanceof JsonObject obj))
            return DataResult.error(() -> "mergeToMap called with non-map: " + map, map);

        List<JsonElement> invalidKeys = new ArrayList<>();

        JsonObject newObj = obj.clone();
        values.entries().forEach(p -> {
            JsonElement key = p.getFirst();
            JsonElement value = p.getSecond();

            if (!(key instanceof JsonPrimitive prim) || !(prim.getValue() instanceof String)) {
                invalidKeys.add(key);
                return;
            }

            newObj.put(prim.asString(), value);
        });

        if (!invalidKeys.isEmpty())
            return DataResult.error(() -> "some keys are not strings: " + invalidKeys, newObj);

        return DataResult.success(newObj);
    }

    @Override
    public DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement input) {
        if (!(input instanceof JsonObject obj)) {
            return DataResult.error(() -> "Not a JSON object: " + input);
        }
        return DataResult.success(obj.entrySet().stream().map(entry -> Pair.of(new JsonPrimitive(entry.getKey()), entry.getValue() instanceof JsonNull ? null : entry.getValue())));
    }

    @Override
    public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> map) {
        JsonObject result = new JsonObject();
        map.forEach(p -> result.put(p.getFirst() instanceof JsonPrimitive prim ? prim.asString() : p.getFirst().toString(), p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<JsonElement>> getStream(JsonElement input) {
        if (input instanceof JsonArray arr) {
            return DataResult.success(arr.stream().map(e -> e instanceof JsonNull ? null : e));
        }
        return DataResult.error(() -> "Not a JSON array: " + input);
    }

    @Override
    public JsonElement createList(Stream<JsonElement> input) {
        final JsonArray result = new JsonArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public JsonElement remove(JsonElement input, String key) {
        if (input instanceof JsonObject obj) {
            final JsonObject result = new JsonObject();
            obj.entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key)).forEach(entry -> result.put(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }

    @Override
    public String toString() {
        return "JSON (Jankson)";
    }
}*/
