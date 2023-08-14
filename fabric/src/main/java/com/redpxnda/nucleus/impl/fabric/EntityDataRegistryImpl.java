package com.redpxnda.nucleus.impl.fabric;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.impl.EntityDataRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityDataRegistryImpl {
    public static final Map<Class<? extends EntityCapability<?>>, Holder<?>> CAPABILITIES = new HashMap<>();
    public static final BiMap<ResourceLocation, Class<? extends EntityCapability<?>>> REGISTERED = HashBiMap.create();
    public static final Multimap<Class<? extends EntityCapability<?>>, EntityDataRegistry.CreationListener<?>> LISTENERS = HashMultimap.create();

    public static <T extends EntityCapability<?>> void register(ResourceLocation id, Predicate<Entity> entity, Class<T> cap, Supplier<T> creator) {
        CAPABILITIES.put(cap, new Holder<>(id, creator, entity));
        REGISTERED.put(id, cap);
    }

    public static Class<? extends EntityCapability<?>> getFromId(ResourceLocation id) {
        return REGISTERED.get(id);
    }

    public static ResourceLocation getIdFrom(Class<? extends EntityCapability<?>> cap) {
        return REGISTERED.inverse().get(cap);
    }

    public static <T extends EntityCapability<?>> void addCreationListener(Class<T> cap, EntityDataRegistry.CreationListener<T> listener) {
        LISTENERS.put(cap, listener);
    }

    public record Holder<T extends EntityCapability<?>>(ResourceLocation id, Supplier<T> constructor, Predicate<Entity> predicate) {
        public T construct() {
            return constructor.get();
        }
    }
}
