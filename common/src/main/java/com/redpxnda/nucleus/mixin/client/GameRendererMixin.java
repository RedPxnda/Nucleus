package com.redpxnda.nucleus.mixin.client;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    /*@Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V")
    )
    private void nucleus$renderItemInHandEvent(float f, long l, PoseStack poseStack, CallbackInfo ci) {

    }*/
}
