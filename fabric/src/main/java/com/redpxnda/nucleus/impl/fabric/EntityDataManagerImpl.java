package com.redpxnda.nucleus.impl.fabric;

import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.fabric.IEntityDataSaver;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class EntityDataManagerImpl {
    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap) {
        String id = EntityDataRegistryImpl.CAPABILITIES.get(cap).id().toString();
        return (T) ((IEntityDataSaver) entity).getCapabilities().get(id);
    }

    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, Supplier<T> ifFailed) {
        String id = EntityDataRegistryImpl.CAPABILITIES.get(cap).id().toString();
        return (T) ((IEntityDataSaver) entity).getCapabilities().getOrDefault(id, ifFailed.get());
    }

    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, T ifFailed) {
        String id = EntityDataRegistryImpl.CAPABILITIES.get(cap).id().toString();
        return (T) ((IEntityDataSaver) entity).getCapabilities().getOrDefault(id, ifFailed);
    }
}
