package com.redpxnda.nucleus.impl.fabric;

import com.redpxnda.nucleus.capability.EntityCapability;
import com.redpxnda.nucleus.fabric.EntityDataSaver;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class EntityDataManagerImpl {
    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap) {
        String id = EntityDataRegistryImpl.CAPABILITIES.get(cap).id().toString();
        return (T) ((EntityDataSaver) entity).getCapabilities().get(id);
    }

    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, Supplier<T> ifFailed) {
        String id = EntityDataRegistryImpl.CAPABILITIES.get(cap).id().toString();
        return (T) ((EntityDataSaver) entity).getCapabilities().getOrDefault(id, ifFailed.get());
    }

    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, T ifFailed) {
        String id = EntityDataRegistryImpl.CAPABILITIES.get(cap).id().toString();
        return (T) ((EntityDataSaver) entity).getCapabilities().getOrDefault(id, ifFailed);
    }

    public static <T extends EntityCapability<?>> T getOrCreateCapability(Entity entity, Class<T> cap) {
        T inst = getCapability(entity, cap);
        if (inst == null) {
            inst = (T) EntityDataRegistryImpl.CAPABILITIES.get(cap).construct();
            ((EntityDataSaver) entity).getCapabilities().put(EntityDataRegistryImpl.getIdFrom(cap).toString(), inst);
        }
        return inst;
    }

    public static <T extends EntityCapability<?>> boolean entityHasCapability(Entity entity, Class<T> cap) {
        return getCapability(entity, cap) != null;
    }
}
