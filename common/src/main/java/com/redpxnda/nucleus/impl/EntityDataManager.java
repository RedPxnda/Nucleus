package com.redpxnda.nucleus.impl;

import com.redpxnda.nucleus.capability.EntityCapability;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public class EntityDataManager {
    @ExpectPlatform
    public static <T extends EntityCapability<?>> T getCapability(Entity entity, Class<T> cap) {
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
}
