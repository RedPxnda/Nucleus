package com.redpxnda.nucleus.datapack.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class MiscCodecs {
    public static <T> Codec<T[]> array(Codec<T> codec, IntFunction<T[]> converter) {
        return codec.listOf().xmap(l -> l.toArray(converter), Arrays::asList);
    }
    public static <T> Codec<T[]> array(Codec<T> codec, Class<T> cls) {
        return codec.listOf().xmap(l -> l.toArray(i -> (T[]) Array.newInstance(cls, i)), Arrays::asList);
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

    public record DoubleRange(double min, double max) {}
}
