package com.redpxnda.nucleus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

@Mixin(ThreadedAnvilChunkStorage.EntityTracker.class)
public interface TrackedEntityAccessor {
    @Accessor
    Set<EntityTrackingListener> getListeners();
}
