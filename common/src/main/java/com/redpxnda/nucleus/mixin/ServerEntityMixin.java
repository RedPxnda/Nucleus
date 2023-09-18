package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.EntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(
            method = "startTracking",
            at = @At("TAIL")
    )
    private void nucleus$trackingChangeEventStartedStage(ServerPlayerEntity player, CallbackInfo ci) {
        EntityEvents.TRACKING_CHANGE.invoker().onTrackingUpdate(EntityEvents.TrackingStage.STARTED, entity, player);
    }

    @Inject(
            method = "stopTracking",
            at = @At("TAIL")
    )
    private void nucleus$trackingChangeEventStoppedStage(ServerPlayerEntity player, CallbackInfo ci) {
        EntityEvents.TRACKING_CHANGE.invoker().onTrackingUpdate(EntityEvents.TrackingStage.STOPPED, entity, player);
    }
}
