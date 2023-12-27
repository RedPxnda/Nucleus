package com.redpxnda.nucleus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.redpxnda.nucleus.event.ClientEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "updateMouse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$playerMoveCameraEvent(CallbackInfo ci) {
        EventResult result = ClientEvents.CAN_MOVE_CAMERA.invoker().call(client);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @WrapOperation(
            method = "updateMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V")
    )
    private void nucleus$modifyCameraMotionEvent(ClientPlayerEntity instance, double xMotion, double yMotion, Operation<Void> original) {
        ClientEvents.CameraMotion motion = new ClientEvents.CameraMotion(xMotion, yMotion);
        ClientEvents.MODIFY_CAMERA_MOTION.invoker().move(client, motion);
        original.call(instance, motion.getXMotion(), motion.getYMotion());
    }
}
