package com.redpxnda.nucleus.impl;

import com.redpxnda.nucleus.capability.EntityCapability;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityDataRegistry {
    @ExpectPlatform
    public static <T extends EntityCapability<?>> void register(ResourceLocation id, Predicate<Entity> entity, Class<T> cap, Supplier<T> creator) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Class<? extends EntityCapability<?>> getFromId(ResourceLocation id) {
        throw new AssertionError();
    }
}
