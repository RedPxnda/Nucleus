package com.redpxnda.nucleus.codec.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntityTypeListCodec implements Codec<EntityTypeList> {
    public static final EntityTypeListCodec INSTANCE = new EntityTypeListCodec();

    protected EntityTypeListCodec() {
    }

    @Override
    public <T> DataResult<Pair<EntityTypeList, T>> decode(DynamicOps<T> ops, T input) {
        List<EntityType<?>> objects = new ArrayList<>();
        List<TagKey<EntityType<?>>> tags = new ArrayList<>();
        List<String> builtins = new ArrayList<>();

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

                        tags.add(TagKey.of(RegistryKeys.ENTITY_TYPE, id));
                    } else if (str.startsWith("$"))
                        builtins.add(str.substring(1));
                    else {
                        Identifier id = Identifier.tryParse(str);
                        if (id == null) {
                            failedValues.add(t);
                            return;
                        }

                        EntityType<?> obj = Registries.ENTITY_TYPE.getOrEmpty(id).orElse(null);
                        if (obj == null) {
                            failedValues.add(t);
                            return;
                        }
                        objects.add(obj);
                    }
                } else failedValues.add(t);
            });

            EntityTypeList result = new EntityTypeList(objects, tags, builtins);
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
            EntityTypeList result = null;
            if (str.startsWith("#")) {
                id = Identifier.tryParse(str.substring(1));
                if (id != null)
                    result = new EntityTypeList(List.of(), List.of(TagKey.of(RegistryKeys.ENTITY_TYPE, id)), List.of());
            } else if (str.startsWith("$"))
                result = new EntityTypeList(List.of(), List.of(), List.of(str.substring(1)));
            else {
                id = Identifier.tryParse(str);
                if (id != null) {
                    EntityType<?> obj = Registries.ENTITY_TYPE.getOrEmpty(id).orElse(null);
                    if (obj != null) result = new EntityTypeList(List.of(obj), List.of(), List.of());
                }
            }

            if (result == null)
                return DataResult.error(() -> "Could not accept value while decoding tag list: Invalid identifier provided! -> " + str);
            return DataResult.success(Pair.of(result, input));
        }

        return DataResult.error(() -> "Failed to create tag list! Not a list or string: " + input);
    }

    @Override
    public <T> DataResult<T> encode(EntityTypeList input, DynamicOps<T> ops, T prefix) {
        List<T> objects = new ArrayList<>();

        input.getObjects().forEach(c -> {
            Identifier id = Registries.ENTITY_TYPE.getId(c);
            objects.add(ops.createString(id.toString()));
        });

        input.getTags().forEach(t -> {
            Identifier id = t.id();
            objects.add(ops.createString('#' + id.toString()));
        });

        input.getBuiltins().forEach(s -> {
            objects.add(ops.createString('$' + s));
        });

        return DataResult.success(ops.createList(objects.stream()));
    }
}
