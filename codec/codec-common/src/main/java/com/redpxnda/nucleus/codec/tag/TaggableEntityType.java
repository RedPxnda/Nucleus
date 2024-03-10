package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;

@CodecBehavior.Override()
public class TaggableEntityType extends TaggableEntry<EntityType<?>> {
    public static final Codec<TaggableEntityType> CODEC = new TaggableEntryCodec<>(TaggableEntityType::new, TaggableEntityType::new, Registries.ENTITY_TYPE, RegistryKeys.ENTITY_TYPE);

    public TaggableEntityType(@NotNull EntityType<?> object) {
        super(object, Registries.ENTITY_TYPE, RegistryKeys.ENTITY_TYPE);
    }

    public TaggableEntityType(@NotNull TagKey<EntityType<?>> tag) {
        super(tag, Registries.ENTITY_TYPE, RegistryKeys.ENTITY_TYPE);
    }
}
