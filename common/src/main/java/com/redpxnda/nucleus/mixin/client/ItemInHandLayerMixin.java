package com.redpxnda.nucleus.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> {
    @Inject(
            method = "renderArmWithItem",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void nucleus$thirdPersonItemRenderEvent(
            LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext,
            HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {

        if ((Object) this instanceof ItemInHandLayer l) {
            EventResult result = RenderEvents.ITEM_HAND_LAYER_RENDER.invoker().render(
                    l.getParentModel(), livingEntity, itemStack, displayContext, arm, poseStack, buffer, packedLight
            );

            if (result.interruptsFurtherEvaluation()) {
                ci.cancel();
                poseStack.popPose();
            }
        }
    }
}
