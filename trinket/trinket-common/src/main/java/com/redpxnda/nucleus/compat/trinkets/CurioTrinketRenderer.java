package com.redpxnda.nucleus.compat.trinkets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public interface CurioTrinketRenderer {
    void render(ItemStack stack, LivingEntity entity, int slotIndex,
                MatrixStack matrixStack, EntityModel<? extends LivingEntity> model, VertexConsumerProvider bufferSource,
                int light, float limbSwing, float limbSwingAmount,
                float tickDelta, float animProgress, float netHeadYaw,
                float headPitch);

    /**
     * Use this to make your trinket's model, which for this method has to be a humanoid/biped entity model, follow an entity's rotations.
     * @param entity the entity
     * @param model  your trinket's model
     */
    static void followBodyRotations(LivingEntity entity, BipedEntityModel<? extends LivingEntity> model) {
        EntityRenderer<? super LivingEntity> render = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        if (render instanceof LivingEntityRenderer livingRenderer) {
            EntityModel<LivingEntity> entityModel = livingRenderer.getModel();
            if (entityModel instanceof BipedEntityModel bipedModel) {
                bipedModel.copyBipedStateTo(model);
            }
        }
    }

    // followiing stuff was yoinked and modified from the TrinketRenderer, so take a look at that
    static <T extends LivingEntity, M extends BipedEntityModel<T>> void translateToFace(MatrixStack matrices, M model, T entity, float headYaw, float headPitch) {
        if (!entity.isInSwimmingPose() && !entity.isFallFlying()) {
            if (entity.isInSneakingPose() && !model.riding) {
                matrices.translate(0.0F, 0.25F, 0.0F);
            }

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));
        } else {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(model.head.roll));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45.0F));
        }

        matrices.translate(0.0F, -0.25F, -0.3F);
    }

    static <T extends LivingEntity, M extends BipedEntityModel<T>> void translateToChest(MatrixStack matrices, M model, T entity) {
        if (entity.isInSneakingPose() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.body.pitch));
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.body.yaw));
        matrices.translate(0.0F, 0.4F, -0.16F);
    }

    static <T extends LivingEntity, M extends BipedEntityModel<T>> void translateToRightArm(MatrixStack matrices, M model, T entity) {
        if (entity.isInSneakingPose() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.body.yaw));
        matrices.translate(-0.3125F, 0.15625F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(model.rightArm.roll));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.rightArm.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.rightArm.pitch));
        matrices.translate(-0.0625F, 0.625F, 0.0F);
    }

    static <T extends LivingEntity, M extends BipedEntityModel<T>> void translateToLeftArm(MatrixStack matrices, M model, T entity) {
        if (entity.isInSneakingPose() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.body.yaw));
        matrices.translate(0.3125F, 0.15625F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(model.leftArm.roll));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.leftArm.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.leftArm.pitch));
        matrices.translate(0.0625F, 0.625F, 0.0F);
    }

    static <T extends LivingEntity, M extends BipedEntityModel<T>> void translateToRightLeg(MatrixStack matrices, M model, T entity) {
        if (entity.isInSneakingPose() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.0F, 0.25F);
        }

        matrices.translate(-0.125F, 0.75F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(model.rightLeg.roll));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.rightLeg.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.rightLeg.pitch));
        matrices.translate(0.0F, 0.75F, 0.0F);
    }

    static <T extends LivingEntity, M extends BipedEntityModel<T>> void translateToLeftLeg(MatrixStack matrices, M model, T entity) {
        if (entity.isInSneakingPose() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.0F, 0.25F);
        }

        matrices.translate(0.125F, 0.75F, 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(model.leftLeg.roll));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.leftLeg.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.leftLeg.pitch));
        matrices.translate(0.0F, 0.75F, 0.0F);
    }
}
