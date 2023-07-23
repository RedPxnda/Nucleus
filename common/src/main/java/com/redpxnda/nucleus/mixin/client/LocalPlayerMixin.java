package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.event.MiscEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(
            method = "canStartSprinting",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$canClientSprintEvent(CallbackInfoReturnable<Boolean> cir) {
        EventResult result = MiscEvents.CAN_CLIENT_SPRINT.invoker().call((Player)(Object) this);
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(false);
    }
}
