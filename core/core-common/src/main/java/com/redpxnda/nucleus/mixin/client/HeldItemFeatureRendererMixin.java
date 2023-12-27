package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> {
    @Inject(
            method = "renderItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void nucleus$thirdPersonItemRenderEvent(
            LivingEntity livingEntity, ItemStack itemStack, ModelTransformationMode displayContext,
            Arm arm, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, CallbackInfo ci) {

        if ((Object) this instanceof HeldItemFeatureRenderer l) {
            EventResult result = RenderEvents.ITEM_HAND_LAYER_RENDER.invoker().render(
                    l.getContextModel(), livingEntity, itemStack, displayContext, arm, poseStack, buffer, packedLight
            );

            if (result.interruptsFurtherEvaluation()) {
                ci.cancel();
                poseStack.pop();
            }
        }
    }
}
