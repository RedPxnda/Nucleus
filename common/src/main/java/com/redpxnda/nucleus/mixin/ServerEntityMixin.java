package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.EntityEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(
            method = "addPairing",
            at = @At("TAIL")
    )
    private void nucleus$trackingChangeEventStartedStage(ServerPlayer player, CallbackInfo ci) {
        EntityEvents.TRACKING_CHANGE.invoker().onTrackingUpdate(EntityEvents.TrackingStage.STARTED, entity, player);
    }

    @Inject(
            method = "removePairing",
            at = @At("TAIL")
    )
    private void nucleus$trackingChangeEventStoppedStage(ServerPlayer player, CallbackInfo ci) {
        EntityEvents.TRACKING_CHANGE.invoker().onTrackingUpdate(EntityEvents.TrackingStage.STOPPED, entity, player);
    }
}
