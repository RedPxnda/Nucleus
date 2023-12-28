package com.redpxnda.nucleus.codec.tag;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

@AutoCodec.Override()
public class ItemList extends TagList<Item> {
    public static final Codec<ItemList> CODEC = getCodec(ItemList::new, Registries.ITEM, RegistryKeys.ITEM);

    public static ItemList of() {
        return new ItemList(List.of(), List.of());
    }

    public static ItemList of(Item... items) {
        return new ItemList(List.of(items), List.of());
    }

    @SafeVarargs
    public static ItemList of(TagKey<Item>... tags) {
        return new ItemList(List.of(), List.of(tags));
    }

    public ItemList(List<Item> objects, List<TagKey<Item>> tags) {
        super(objects, tags, Registries.ITEM, RegistryKeys.ITEM);
    }

    public boolean contains(ItemStack stack) {
        return contains(stack.getItem());
    }
}
