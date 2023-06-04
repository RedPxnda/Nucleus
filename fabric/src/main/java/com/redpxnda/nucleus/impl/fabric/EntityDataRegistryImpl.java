package com.redpxnda.nucleus.impl.fabric;

import com.redpxnda.nucleus.capability.EntityCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public class EntityDataRegistryImpl {
    public static final Map<Class<? extends EntityCapability>, Holder<?>> CAPABILITIES = new HashMap<>();
    public static final Map<ResourceLocation, Class<? extends EntityCapability>> REGISTERED = new HashMap<>();

    public static <T extends EntityCapability> void register(ResourceLocation id, Predicate<Entity> entity, Class<T> cap, Supplier<T> creator) {
        CAPABILITIES.put(cap, new Holder<>(id, creator, entity));
        REGISTERED.put(id, cap);
    }

    public static Class<? extends EntityCapability> getFromId(ResourceLocation id) {
        return REGISTERED.get(id);
    }

    public record Holder<T extends EntityCapability>(ResourceLocation id, Supplier<T> constructor, Predicate<Entity> predicate) {
        public T construct() {
            return constructor.get();
        }
    }
}
