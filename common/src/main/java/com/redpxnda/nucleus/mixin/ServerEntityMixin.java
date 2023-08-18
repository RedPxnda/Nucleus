package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.EntityEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Inject(
            method = "addPairing",
            at = @At("TAIL")
    )
    private void nucleus$trackingChangeEventStartedStage(ServerPlayer player, CallbackInfo ci) {
        EntityEvents.TRACKING_CHANGE.invoker().onTrackingUpdate(EntityEvents.TrackingStage.STARTED, (Entity) (Object) this, player);
    }

    @Inject(
            method = "removePairing",
            at = @At("TAIL")
    )
    private void nucleus$trackingChangeEventStoppedStage(ServerPlayer player, CallbackInfo ci) {
        EntityEvents.TRACKING_CHANGE.invoker().onTrackingUpdate(EntityEvents.TrackingStage.STOPPED, (Entity) (Object) this, player);
    }
}
