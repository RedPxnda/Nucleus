package com.redpxnda.nucleus.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.redpxnda.nucleus.util.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class JsoncOps  implements DynamicOps<JsoncElement> {
    public static final JsoncOps INSTANCE = new JsoncOps();

    protected JsoncOps() {}

    @Override
    public JsoncElement empty() {
        return JsoncNull.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsoncElement input) {
        if (input instanceof JsoncObject)
            return convertMap(outOps, input);
        if (input instanceof JsoncArray)
            return convertList(outOps, input);
        if (input instanceof JsoncNull)
            return outOps.empty();
        JsoncPrimitive primitive = (JsoncPrimitive) input;
        if (primitive.getValue() instanceof String string)
            return outOps.createString(string);
        if (primitive.getValue() instanceof Boolean bl)
            return outOps.createBoolean(bl);
        if (primitive.getValue() instanceof Number num)
            return outOps.createNumeric(num);
        return null;
    }

    @Override
    public DataResult<Number> getNumberValue(JsoncElement input) {
        if (input instanceof JsoncPrimitive primitive && primitive.getValue() instanceof Number num)
            return DataResult.success(num);
        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public JsoncElement createNumeric(Number i) {
        return new JsoncPrimitive(i);
    }

    @Override
    public DataResult<String> getStringValue(JsoncElement input) {
        if (input instanceof JsoncPrimitive primitive && primitive.getValue() instanceof String str)
            return DataResult.success(str);
        return DataResult.error(() -> "Not a string: " + input);
    }

    @Override
    public JsoncElement createString(String value) {
        return new JsoncPrimitive(value);
    }

    @Override
    public DataResult<JsoncElement> mergeToList(JsoncElement list, JsoncElement value) {
        if (!(list instanceof JsoncArray) && list != empty())
            return DataResult.error(() -> "mergeToList called with non-list: " + list, list);

        JsoncArray newArray = list instanceof JsoncArray array ? array.copy() : new JsoncArray();
        newArray.add(value);
        return DataResult.success(newArray);
    }

    @Override
    public DataResult<JsoncElement> mergeToList(JsoncElement list, List<JsoncElement> values) {
        if (!(list instanceof JsoncArray) && list != empty())
            return DataResult.error(() -> "mergeToList called with non-list: " + list, list);

        JsoncArray newArray = list instanceof JsoncArray array ? array.copy() : new JsoncArray();
        newArray.getRaw().addAll(values);
        return DataResult.success(newArray);
    }

    @Override
    public DataResult<JsoncElement> mergeToMap(JsoncElement map, JsoncElement key, JsoncElement value) {
        if (!(map instanceof JsoncObject) && map != empty())
            return DataResult.error(() -> "mergeToMap called with non-map: " + map, map);
        if (!(key instanceof JsoncPrimitive prim) || !(prim.getValue() instanceof String))
            return DataResult.error(() -> "mergeToMap called with non-string as key: " + key, map);

        JsoncObject newObj = map instanceof JsoncObject obj ? obj.copy() : new JsoncObject();
        newObj.add(prim.getAsString(), value);
        return DataResult.success(newObj);
    }

    @Override
    public DataResult<JsoncElement> mergeToMap(JsoncElement map, MapLike<JsoncElement> values) {
        if (!(map instanceof JsoncObject) && map != empty())
            return DataResult.error(() -> "mergeToMap (several values) called with non-map: " + map, map);

        List<JsoncElement> invalidKeys = new ArrayList<>();

        JsoncObject newObj = map instanceof JsoncObject obj ? obj.copy() : new JsoncObject();
        values.entries().forEach(p -> {
            JsoncElement key = p.getFirst();
            JsoncElement value = p.getSecond();

            if (!(key instanceof JsoncPrimitive prim) || !(prim.getValue() instanceof String)) {
                invalidKeys.add(key);
                return;
            }

            newObj.add(prim.getAsString(), value);
        });

        if (!invalidKeys.isEmpty())
            return DataResult.error(() -> "some keys are not strings: " + invalidKeys, newObj);

        return DataResult.success(newObj);
    }

    @Override
    public DataResult<Stream<Pair<JsoncElement, JsoncElement>>> getMapValues(JsoncElement input) {
        if (!(input instanceof JsoncObject obj)) {
            return DataResult.error(() -> "Not a Jsonc object: " + input);
        }
        return DataResult.success(obj.entrySet().stream().map(entry -> Pair.of(new JsoncPrimitive(entry.getKey()), entry.getValue() instanceof JsoncNull ? null : entry.getValue())));
    }

    @Override
    public JsoncElement createMap(Stream<Pair<JsoncElement, JsoncElement>> map) {
        JsoncObject result = new JsoncObject();
        map.forEach(p -> result.add(p.getFirst() instanceof JsoncPrimitive prim ? prim.getAsString() : p.getFirst().toString(), p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<JsoncElement>> getStream(JsoncElement input) {
        if (input instanceof JsoncArray arr) {
            return DataResult.success(arr.getRaw().stream().map(e -> e instanceof JsoncNull ? null : e));
        }
        return DataResult.error(() -> "Not a Jsonc array: " + input);
    }

    @Override
    public JsoncElement createList(Stream<JsoncElement> input) {
        final JsoncArray result = new JsoncArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public JsoncElement remove(JsoncElement input, String key) {
        if (input instanceof JsoncObject obj) {
            final JsoncObject result = new JsoncObject();
            obj.entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key)).forEach(entry -> result.add(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }

    @Override
    public String toString() {
        return "JSONC";
    }
}
