package com.redpxnda.nucleus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.redpxnda.nucleus.event.ClientEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
        EventResult result = ClientEvents.CAN_MOVE_CAMERA.invoker().call(minecraft);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @WrapOperation(
            method = "turnPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V")
    )
    private void nucleus$modifyCameraMotionEvent(LocalPlayer instance, double xMotion, double yMotion, Operation<Void> original) {
        ClientEvents.CameraMotion motion = new ClientEvents.CameraMotion(xMotion, yMotion);
        ClientEvents.MODIFY_CAMERA_MOTION.invoker().move(minecraft, motion);
        original.call(instance, motion.getXMotion(), motion.getYMotion());
    }
}
