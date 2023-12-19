package com.redpxnda.nucleus.util.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.AutoCodec;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

@AutoCodec.Override()
public class EntityTypeList extends TagList<EntityType<?>> {
    public static final Codec<EntityTypeList> CODEC = getCodec(EntityTypeList::new, Registries.ENTITY_TYPE, RegistryKeys.ENTITY_TYPE);

    public static EntityTypeList of() {
        return new EntityTypeList(List.of(), List.of());
    }

    public static EntityTypeList of(EntityType<?>... entities) {
        return new EntityTypeList(List.of(entities), List.of());
    }

    @SafeVarargs
    public static EntityTypeList of(TagKey<EntityType<?>>... tags) {
        return new EntityTypeList(List.of(), List.of(tags));
    }

    protected EntityTypeList(List<EntityType<?>> objects, List<TagKey<EntityType<?>>> tags) {
        super(objects, tags, Registries.ENTITY_TYPE, RegistryKeys.ENTITY_TYPE);
    }
}
