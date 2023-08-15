package com.redpxnda.nucleus.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow public abstract M getModel();

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$livingRenderPreEvent(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        EventResult result = RenderEvents.LIVING.invoker().render(RenderEvents.Stage.PRE, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL")
    )
    private void nucleus$livingRenderPostEvent(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        RenderEvents.LIVING.invoker().render(RenderEvents.Stage.POST, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER),
            cancellable = true)
    private void nucleus$livingRenderPushEvent(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        EventResult result = RenderEvents.LIVING.invoker().render(RenderEvents.Stage.PUSHED, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }
    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER),
            cancellable = true)
    private void nucleus$livingRenderSetupPoseEvent(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        EventResult result = RenderEvents.LIVING.invoker().render(RenderEvents.Stage.POSE_SETUP, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

}
