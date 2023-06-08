package com.redpxnda.nucleus.impl.forge;

import com.redpxnda.nucleus.capability.EntityCapability;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class EntityDataManagerImpl {
    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap) {
        return (T) entity.getCapability(EntityDataRegistryImpl.CAPABILITIES.get(cap).capability).resolve().get();
    }

    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, Supplier<T> ifFailed) {
        Optional<?> optional = entity.getCapability(EntityDataRegistryImpl.CAPABILITIES.get(cap).capability).resolve();
        return optional.map(o -> (T) o).orElseGet(ifFailed);
    }

    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, T ifFailed) {
        Optional<?> optional = entity.getCapability(EntityDataRegistryImpl.CAPABILITIES.get(cap).capability).resolve();
        return optional.map(o -> (T) o).orElse(ifFailed);
    }
}
