package com.redpxnda.nucleus.capability.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class EntityDataRegistry {
    public static final Map<Class<? extends EntityCapability<?>>, Holder<?>> CAPABILITIES = new HashMap<>();
    public static final BiMap<ResourceLocation, Class<? extends EntityCapability<?>>> REGISTERED = HashBiMap.create();

    public static <T extends EntityCapability<?>> void register(ResourceLocation id, Predicate<Entity> entity, Class<T> cap, Function<Entity, T> creator) {
        CAPABILITIES.put(cap, new Holder<>(id, creator, entity));
        REGISTERED.put(id, cap);
    }

    public static Class<? extends EntityCapability<?>> getFromId(ResourceLocation id) {
        return REGISTERED.get(id);
    }

    public static ResourceLocation getIdFrom(Class<? extends EntityCapability<?>> cap) {
        return REGISTERED.inverse().get(cap);
    }

    public record Holder<T extends EntityCapability<?>>(ResourceLocation id, Function<Entity, T> constructor, Predicate<Entity> predicate) {
        public T construct(Entity entity) {
            return constructor.apply(entity);
        }
    }

    /*@ExpectPlatform
    public static <T extends EntityCapability<?>> void register(ResourceLocation id, Predicate<Entity> entity, Class<T> cap, Supplier<T> creator) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Class<? extends EntityCapability<?>> getFromId(ResourceLocation id) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getIdFrom(Class<? extends EntityCapability<?>> cap) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends EntityCapability<?>> void addCreationListener(Class<T> cap, CreationListener<T> listener) {
        throw new AssertionError();
    }*/

    public interface CreationListener<T extends EntityCapability<?>> {
        void onCreate(Entity entity, T cap);
    }
}
