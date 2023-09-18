package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow public abstract M getModel();

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$livingRenderPreEvent(T livingEntity, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci) {
        EventResult result = RenderEvents.LIVING_ENTITY_RENDER.invoker().render(RenderEvents.EntityRenderStage.PRE, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("TAIL")
    )
    private void nucleus$livingRenderPostEvent(T livingEntity, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci) {
        RenderEvents.LIVING_ENTITY_RENDER.invoker().render(RenderEvents.EntityRenderStage.POST, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER),
            cancellable = true)
    private void nucleus$livingRenderPushEvent(T livingEntity, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci) {
        EventResult result = RenderEvents.LIVING_ENTITY_RENDER.invoker().render(RenderEvents.EntityRenderStage.PUSHED, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }
    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER),
            cancellable = true)
    private void nucleus$livingRenderSetupPoseEvent(T livingEntity, float f, float g, MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, CallbackInfo ci) {
        EventResult result = RenderEvents.LIVING_ENTITY_RENDER.invoker().render(RenderEvents.EntityRenderStage.POSE_SETUP, getModel(), livingEntity, f, g, poseStack, multiBufferSource, i);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

}
