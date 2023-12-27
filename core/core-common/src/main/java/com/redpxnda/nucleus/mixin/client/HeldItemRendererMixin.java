package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.client.ArmRenderer;
import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    /*@WrapOperation(
            method = "renderItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;getHandRenderType(Lnet/minecraft/client/network/ClientPlayerEntity;)Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;")
    )
    private HeldItemRenderer.HandRenderType nucleus$changeRenderedHandsEvent(ClientPlayerEntity player, Operation<HeldItemRenderer.HandRenderType> original) {
        HeldItemRenderer.HandRenderType selection = original.call(player);
        RenderEvents.RenderedHands hands = switch (selection) {
            case RENDER_BOTH_HANDS -> RenderEvents.RenderedHands.BOTH.copy();
            case RENDER_MAIN_HAND_ONLY -> RenderEvents.RenderedHands.MAINHAND.copy();
            case RENDER_OFF_HAND_ONLY -> RenderEvents.RenderedHands.OFFHAND.copy();
        };
        RenderEvents.CHANGE_RENDERED_HANDS.invoker().evaluate(player, hands);
        if (hands.hasBeenModified()) {
            selection =
                    hands.hasMainhand() && hands.hasOffhand() ? HeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS :
                    hands.hasMainhand() ? HeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY :
                    HeldItemRenderer.HandRenderType.RENDER_OFF_HAND_ONLY;
        }
        return selection;
    }*/

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void nucleus$renderArmEventPushStage(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand hand, float swingProgress, ItemStack stack, float equippedProgress, MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, CallbackInfo ci) {
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.PUSHED, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"),
            cancellable = true
    )
    private void nucleus$renderArmEventArmStage(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand hand, float swingProgress, ItemStack stack, float equippedProgress, MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, CallbackInfo ci) {
        if (!stack.isEmpty()) return;
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.ARM, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
            cancellable = true
    )
    private void nucleus$renderArmEventItemStage(AbstractClientPlayerEntity player, float partialTicks, float pitch, Hand hand, float swingProgress, ItemStack stack, float equippedProgress, MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, CallbackInfo ci) {
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.ITEM, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    private boolean nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage stage,
                                                   AbstractClientPlayerEntity player, float partialTicks, float pitch,
                                                   Hand hand, float swingProgress, ItemStack stack,
                                                   float equippedProgress, MatrixStack poseStack, VertexConsumerProvider buffer,
                                                   int combinedLight
    ) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm humanoidArm = bl ? player.getMainArm() : player.getMainArm().getOpposite();

        ArmRenderer armRenderer = new ArmRenderer((poseStack2, buffer2, combinedLight2, equippedProgress2, swingProgress2, side2) -> {
            HeldItemRenderer heldItemRenderer = (HeldItemRenderer) (Object) this;
            ((HeldItemRendererAccessor) heldItemRenderer).callRenderArmHoldingItem(poseStack2, buffer2, combinedLight2, equippedProgress2, swingProgress2, side2);
        }, poseStack, buffer, combinedLight, equippedProgress, swingProgress, humanoidArm);
        EventResult eventResult = RenderEvents.RENDER_ARM_WITH_ITEM.invoker().render(
                stage, armRenderer, player, poseStack, buffer, stack, hand, partialTicks, pitch, swingProgress, equippedProgress, combinedLight
        );
        if (stage.shouldInterrupt() && eventResult.interruptsFurtherEvaluation()) {
            poseStack.pop();
            return true;
        }
        return false;
    }
}
