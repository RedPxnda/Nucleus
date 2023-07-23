package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.event.MiscEvents;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "turnPlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$playerMoveCameraEvent(CallbackInfo ci) {
        EventResult result = MiscEvents.MOVE_PLAYER_CAMERA.invoker().call(minecraft);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @ModifyVariable(method = "turnPlayer", ordinal = 1, at = @At("STORE"))
    private double nucleus$modifyCameraSensitivityEvent(double value) {
        CompoundEventResult<Double> result = MiscEvents.MODIFY_CAMERA_SENSITIVITY.invoker().modify(minecraft, value);
        if (result.interruptsFurtherEvaluation())
            return result.object();
        return value;
    }
}
