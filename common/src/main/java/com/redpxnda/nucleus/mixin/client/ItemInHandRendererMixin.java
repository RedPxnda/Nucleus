package com.redpxnda.nucleus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.client.ArmRenderer;
import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow protected abstract void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, float equippedProgress, float swingProgress, HumanoidArm side);

    @WrapOperation(
            method = "renderHandsWithItems",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;evaluateWhichHandsToRender(Lnet/minecraft/client/player/LocalPlayer;)Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;")
    )
    private ItemInHandRenderer.HandRenderSelection nucleus$changeRenderedHandsEvent(LocalPlayer player, Operation<ItemInHandRenderer.HandRenderSelection> original) {
        ItemInHandRenderer.HandRenderSelection selection = original.call(player);
        RenderEvents.RenderedHands hands = switch (selection) {
            case RENDER_BOTH_HANDS -> RenderEvents.RenderedHands.BOTH.copy();
            case RENDER_MAIN_HAND_ONLY -> RenderEvents.RenderedHands.MAINHAND.copy();
            case RENDER_OFF_HAND_ONLY -> RenderEvents.RenderedHands.OFFHAND.copy();
        };
        RenderEvents.CHANGE_RENDERED_HANDS.invoker().evaluate(player, hands);
        if (hands.hasBeenModified()) {
            selection =
                    hands.hasMainhand() && hands.hasOffhand() ? ItemInHandRenderer.HandRenderSelection.RENDER_BOTH_HANDS :
                    hands.hasMainhand() ? ItemInHandRenderer.HandRenderSelection.RENDER_MAIN_HAND_ONLY :
                    ItemInHandRenderer.HandRenderSelection.RENDER_OFF_HAND_ONLY;
        }
        return selection;
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void nucleus$renderArmEventPushStage(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.PUSHED, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"),
            cancellable = true
    )
    private void nucleus$renderArmEventArmStage(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (!stack.isEmpty()) return;
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.ARM, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"),
            cancellable = true
    )
    private void nucleus$renderArmEventItemStage(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        if (nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage.ITEM, player, partialTicks, pitch, hand, swingProgress, stack, equippedProgress, poseStack, buffer, combinedLight))
            ci.cancel();
    }

    private boolean nucleus$renderArmWithItemEvent(RenderEvents.ArmRenderStage stage,
            AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack,
            float equippedProgress, PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight
    ) {
        boolean bl = hand == InteractionHand.MAIN_HAND;
        HumanoidArm humanoidArm = bl ? player.getMainArm() : player.getMainArm().getOpposite();

        ArmRenderer armRenderer = new ArmRenderer(this::renderPlayerArm, poseStack, buffer, combinedLight, equippedProgress, swingProgress, humanoidArm);
        EventResult eventResult = RenderEvents.RENDER_ARM_WITH_ITEM.invoker().render(
                stage, armRenderer, player, poseStack, buffer, stack, hand, partialTicks, pitch, swingProgress, equippedProgress, combinedLight
        );
        if (stage.shouldInterrupt() && eventResult.interruptsFurtherEvaluation()) {
            poseStack.popPose();
            return true;
        }
        return false;
    }
}
