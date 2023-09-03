package com.redpxnda.nucleus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> {
    @WrapOperation(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/ItemInHandLayer;renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    private void nucleus$itemHandLayerEvent(
            ItemInHandLayer<T, M> instance, LivingEntity livingEntity, ItemStack itemStack,
            ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, Operation<Void> original) {
        EventResult result = RenderEvents.ITEM_HAND_LAYER_RENDER.invoker().render(
                arm, livingEntity, itemStack, displayContext, poseStack, buffer, packedLight
        );

        if (!result.interruptsFurtherEvaluation()) original.call(instance, livingEntity, itemStack, displayContext, arm, poseStack, buffer, packedLight);
    }
}
