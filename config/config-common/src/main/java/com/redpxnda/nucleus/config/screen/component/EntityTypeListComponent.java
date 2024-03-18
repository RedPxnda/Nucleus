package com.redpxnda.nucleus.config.screen.component;

import com.google.common.collect.HashBiMap;
import com.redpxnda.nucleus.codec.tag.EntityTypeList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;

import java.util.stream.Collectors;

public class EntityTypeListComponent extends TagListComponent<EntityType<?>, EntityTypeList> {
    public final PredicateEntryType predicateEntryType = new PredicateEntryType();

    public EntityTypeListComponent(int x, int y) {
        super(EntityTypeList::of, Registries.ENTITY_TYPE, RegistryKeys.ENTITY_TYPE, "entity", x, y);
        entryTypes.put("predicate", predicateEntryType);
    }

    @Override
    public void setValue(EntityTypeList value) {
        super.setValue(value);
        value.getBuiltins().forEach(str -> components.add(new Entry(predicateEntryType, str)));
    }

    public class PredicateEntryType extends EntryType<String> {
        @Override
        public ConfigComponent<String> createEntry() {
            return new DropdownComponent<>(MinecraftClient.getInstance().textRenderer, 0, 0, 100, 20,
                    HashBiMap.create(EntityTypeList.builtinPredicates.keySet().stream().collect(Collectors.toMap(v -> v, v -> v))));
        }

        @Override
        public void addToList(EntityTypeList list, String value) {
            list.getBuiltins().add(value);
        }
    }
}
