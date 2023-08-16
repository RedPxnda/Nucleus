package com.redpxnda.nucleus.datapack.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class MiscCodecs {
    public static final ColorCodec COLOR = ColorCodec.INSTANCE;
    public static final Codec<Vector3f> VECTOR_3F = PolyCodec.of(
            Codec.FLOAT.listOf().comapFlatMap(
                    list -> {
                        if (list.size() != 3)
                            return DataResult.error(() -> "Invalid array size for 3d vector! Size must be 3! Size: " + list.size());
                        return DataResult.success(new Vector3f(list.get(0), list.get(1), list.get(2)));
                    },
                    vec -> List.of(vec.x, vec.y, vec.z)
            ),
            Codec.simpleMap(Codec.STRING, Codec.FLOAT, Keyable.forStrings(() -> Stream.of("x", "y", "z"))).xmap(
                    map -> new Vector3f(map.get("x"), map.get("y"), map.get("z")),
                    vec -> Map.of("x", vec.x, "y", vec.y, "z", vec.z)
            ).codec()
    );

    public static <T> Codec<T[]> array(Codec<T> codec, IntFunction<T[]> converter) {
        return codec.listOf().xmap(l -> l.toArray(converter), Arrays::asList);
    }
    public static <T> Codec<T[]> array(Codec<T> codec, Class<T> cls) {
        return codec.listOf().xmap(l -> l.toArray(i -> (T[]) Array.newInstance(cls, i)), Arrays::asList);
    }

    public static <T> T quickParse(JsonElement element, Codec<T> codec, Consumer<String> ifFailed) {
        return codec.parse(JsonOps.INSTANCE, element).getOrThrow(false, ifFailed);
    }
    public static <T, O> T quickParse(DynamicOps<O> ops, O element, Codec<T> codec, Consumer<String> ifFailed) {
        return codec.parse(ops, element).getOrThrow(false, ifFailed);
    }

    public static final Codec<DoubleRange> DOUBLE_RANGE = Codec.either(
            Codec.DOUBLE.listOf(),
            Codec.pair(Codec.DOUBLE.fieldOf("min").codec(), Codec.DOUBLE.fieldOf("max").codec())
    )
            .xmap(e -> {
                if (e.left().isPresent()) {
                    List<Double> doubleList = e.left().get();
                    return new DoubleRange(doubleList.get(0), doubleList.get(1));
                } else {
                    Pair<Double, Double> pair = e.right().get();
                    return new DoubleRange(pair.getFirst(), pair.getSecond());
                }
            }, dr -> Either.right(Pair.of(dr.min, dr.max)));

    public static <T extends Enum<T>> Codec<T> ofEnum(Class<T> cls) {
        return Codec.STRING.xmap(s -> T.valueOf(cls, s), Enum::name);
    }

    public static <T extends Enum<T>> Codec<T> ofEnum(Class<T> cls, Function<String, T> convert) {
        return Codec.STRING.xmap(convert, Enum::name);
    }

    public static <T extends Consumer<JsonElement>> Codec<ConsumerHolder<T>> consumerMapCodec(String typeKey, Map<String, T> map) {
        return ExtraCodecs.JSON.comapFlatMap(json -> {
            if (json instanceof JsonPrimitive prim) {
                String name = prim.getAsString();
                return DataResult.success(new ConsumerHolder<>(map.get(name), null, name));
            } else if (json instanceof JsonObject object) {
                if (!object.has(typeKey))
                    return DataResult.error(() -> "No member '" + typeKey + "' found in '" + object + "'!");
                String name = object.get(typeKey).getAsString();
                return DataResult.success(new ConsumerHolder<>(map.get(name), object, name));
            } else
                return DataResult.error(() -> "Not an object nor string '" + json + "'!");
        }, holder -> holder.element == null ? new JsonPrimitive(holder.name) : holder.element);
    }
    public record ConsumerHolder<T extends Consumer<JsonElement>>(T consumer, @Nullable JsonElement element, String name) {}

    public static <A, T extends Function<JsonElement, A>> Codec<FunctionHolder<A, T>> functionMapCodec(String typeKey, Map<String, T> map) {
        return ExtraCodecs.JSON.comapFlatMap(json -> {
            if (json instanceof JsonPrimitive prim) {
                String name = prim.getAsString();
                return DataResult.success(new FunctionHolder<>(map.get(name), null, name));
            } else if (json instanceof JsonObject object) {
                if (!object.has(typeKey))
                    return DataResult.error(() -> "No member '" + typeKey + "' found in '" + object + "'!");
                String name = object.get(typeKey).getAsString();
                return DataResult.success(new FunctionHolder<>(map.get(name), object, name));
            } else
                return DataResult.error(() -> "Not an object nor string '" + json + "'!");
        }, holder -> holder.element == null ? new JsonPrimitive(holder.name) : holder.element);
    }
    public record FunctionHolder<A, T extends Function<JsonElement, A>>(T func, JsonElement element, String name) implements Function<JsonElement, A> {
        @Override
        public A apply(JsonElement element) {
            return func.apply(element);
        }
        public A apply() {
            return func.apply(element);
        }
    }
    public static <A, T extends Function<JsonElement, A>, O> A dispatchFunctionMap(DynamicOps<O> ops, O element, String typeKey, Map<String, T> map, Consumer<String> errorMessage) {
        FunctionHolder<A, T> functionHolder = functionMapCodec(typeKey, map).parse(ops, element)
                .getOrThrow(false, errorMessage);
        return functionHolder.apply();
    }
    public static <A, T extends Function<JsonElement, A>> A dispatchFunctionMap(JsonElement element, String typeKey, Map<String, T> map, Consumer<String> errorMessage) {
        FunctionHolder<A, T> functionHolder = functionMapCodec(typeKey, map).parse(JsonOps.INSTANCE, element)
                .getOrThrow(false, errorMessage);
        return functionHolder.apply();
    }

    public record DoubleRange(double min, double max) {}
}
