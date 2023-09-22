package com.redpxnda.nucleus.capability.entity;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EntityDataManager {
    public static <T extends EntityCapability<?>> @Nullable T getCapability(Entity entity, Class<T> cap) {
        String id = EntityDataRegistry.CAPABILITIES.get(cap).id().toString();
        return (T) ((EntityDataSaver) entity).getCapabilities().get(id);
    }

    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, T ifFailed) {
        String id = EntityDataRegistry.CAPABILITIES.get(cap).id().toString();
        return (T) ((EntityDataSaver) entity).getCapabilities().getOrDefault(id, ifFailed);
    }

    public static <T extends EntityCapability<?>> T getOrCreateCapability(Entity entity, Class<T> cap) {
        T inst = getCapability(entity, cap);
        if (inst == null) {
            inst = (T) EntityDataRegistry.CAPABILITIES.get(cap).construct(entity);
            ((EntityDataSaver) entity).getCapabilities().put(EntityDataRegistry.getIdFrom(cap).toString(), inst);
        }
        return inst;
    }

    public static <T extends EntityCapability<?>> T getOrCreateCapability(Entity entity, Class<T> cap, T ifFailed) {
        T inst = getCapability(entity, cap);
        if (inst == null) {
            inst = ifFailed;
            ((EntityDataSaver) entity).getCapabilities().put(EntityDataRegistry.getIdFrom(cap).toString(), inst);
        }
        return inst;
    }

    public static <T extends EntityCapability<?>> boolean canHaveCapability(Entity entity, Class<T> cap) {
        return EntityDataRegistry.CAPABILITIES.get(cap).predicate().test(entity);
    }

    /*@ExpectPlatform
    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends EntityCapability<?>> T getOrCreateCapability(Entity entity, Class<T> cap) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, T ifFailed) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap, Supplier<T> ifFailed) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends EntityCapability<?>> boolean entityHasCapability(Entity entity, Class<T> cap) {
        throw new AssertionError();
    }*/
}
