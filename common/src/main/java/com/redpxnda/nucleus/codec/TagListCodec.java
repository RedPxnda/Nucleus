package com.redpxnda.nucleus.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.util.tag.TagList;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public record TagListCodec<C, L extends TagList<C>>(BiFunction<List<C>, List<TagKey<C>>, L> creator,
                                                    Registry<C> registry,
                                                    RegistryKey<? extends Registry<C>> registryKey) implements Codec<L> {

    @Override
    public <T> DataResult<Pair<L, T>> decode(DynamicOps<T> ops, T input) {
        List<C> objects = new ArrayList<>();
        List<TagKey<C>> tags = new ArrayList<>();

        var potentialList = ops.getStream(input);
        if (potentialList.result().isPresent()) {
            Stream<T> stream = potentialList.result().get();
            List<T> failedValues = new ArrayList<>();
            stream.forEach(t -> {
                var potentialStr = ops.getStringValue(t);
                if (potentialStr.result().isPresent()) {
                    String str = potentialStr.result().get();
                    if (str.startsWith("#")) {
                        Identifier id = Identifier.tryParse(str.substring(1));
                        if (id == null) {
                            failedValues.add(t);
                            return;
                        }

                        tags.add(TagKey.of(registryKey, id));
                    } else {
                        Identifier id = Identifier.tryParse(str);
                        if (id == null) {
                            failedValues.add(t);
                            return;
                        }

                        C obj = registry.getOrEmpty(id).orElse(null);
                        if (obj == null) {
                            failedValues.add(t);
                            return;
                        }
                        objects.add(obj);
                    }
                } else failedValues.add(t);
            });

            L result = creator.apply(objects, tags);
            if (!failedValues.isEmpty())
                return DataResult.error(
                        () -> "Could not accept values while decoding tag list: Invalid identifiers found! -> " + failedValues,
                        Pair.of(result, input));
            else
                return DataResult.success(Pair.of(result, input));
        }

        var potentialStr = ops.getStringValue(input);
        if (potentialStr.result().isPresent()) {
            String str = potentialStr.result().get();
            Identifier id;
            L result = null;
            if (str.startsWith("#")) {
                id = Identifier.tryParse(str.substring(1));
                if (id != null) result = creator.apply(List.of(), List.of(TagKey.of(registryKey, id)));
            } else {
                id = Identifier.tryParse(str);
                if (id != null) {
                    C obj = registry.getOrEmpty(id).orElse(null);
                    if (obj != null) result = creator.apply(List.of(obj), List.of());
                }
            }

            if (result == null)
                return DataResult.error(() -> "Could not accept value while decoding tag list: Invalid identifier provided! -> " + str);
            return DataResult.success(Pair.of(result, input));
        }

        return DataResult.error(() -> "Failed to create tag list! Not a list or string: " + input);
    }

    @Override
    public <T> DataResult<T> encode(L input, DynamicOps<T> ops, T prefix) {
        List<T> objects = new ArrayList<>();

        input.getObjects().forEach(c -> {
            Identifier id = registry.getId(c);
            if (id != null) objects.add(ops.createString(id.toString()));
        });

        input.getTags().forEach(t -> {
            Identifier id = t.id();
            objects.add(ops.createString('#' + id.toString()));
        });

        return DataResult.success(ops.createList(objects.stream()));
    }
}
