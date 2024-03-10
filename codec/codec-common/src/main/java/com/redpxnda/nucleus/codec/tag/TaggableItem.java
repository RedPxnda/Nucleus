package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.NotNull;

@CodecBehavior.Override()
public class TaggableItem extends TaggableEntry<Item> {
    public static final Codec<TaggableItem> CODEC = new TaggableEntryCodec<>(TaggableItem::new, TaggableItem::new, Registries.ITEM, RegistryKeys.ITEM);

    public TaggableItem(@NotNull Item object) {
        super(object, Registries.ITEM, RegistryKeys.ITEM);
    }

    public TaggableItem(@NotNull TagKey<Item> tag) {
        super(tag, Registries.ITEM, RegistryKeys.ITEM);
    }
}
