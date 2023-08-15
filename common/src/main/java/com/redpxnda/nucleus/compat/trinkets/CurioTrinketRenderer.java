package com.redpxnda.nucleus.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface CurioTrinketRenderer {
    void render(ItemStack stack, LivingEntity entity, int slotIndex,
                PoseStack matrixStack, EntityModel<? extends LivingEntity> model, MultiBufferSource bufferSource,
                int light, float limbSwing, float limbSwingAmount,
                float tickDelta, float animProgress, float netHeadYaw,
                float headPitch);

    /**
     * Use this to make your trinket's model, which for this method has to be a humanoid/biped entity model, follow an entity's rotations.
     * @param entity the entity
     * @param model  your trinket's model
     */
    static void followBodyRotations(LivingEntity entity, HumanoidModel<? extends LivingEntity> model) {
        EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        if (render instanceof LivingEntityRenderer livingRenderer) {
            EntityModel<LivingEntity> entityModel = livingRenderer.getModel();
            if (entityModel instanceof HumanoidModel bipedModel) {
                bipedModel.copyPropertiesTo(model);
            }
        }
    }

    // followiing stuff was yoinked and modified from the TrinketRenderer, so take a look at that
    static <T extends LivingEntity, M extends HumanoidModel<T>> void translateToFace(PoseStack matrices, M model, T entity, float headYaw, float headPitch) {
        if (!entity.isVisuallySwimming() && !entity.isFallFlying()) {
            if (entity.isCrouching() && !model.riding) {
                matrices.translate(0.0F, 0.25F, 0.0F);
            }

            matrices.mulPose(Axis.YP.rotationDegrees(headYaw));
            matrices.mulPose(Axis.XP.rotationDegrees(headPitch));
        } else {
            matrices.mulPose(Axis.ZP.rotationDegrees(model.head.zRot));
            matrices.mulPose(Axis.YP.rotationDegrees(headYaw));
            matrices.mulPose(Axis.XP.rotationDegrees(-45.0F));
        }

        matrices.translate(0.0F, -0.25F, -0.3F);
    }

    static <T extends LivingEntity, M extends HumanoidModel<T>> void translateToChest(PoseStack matrices, M model, T entity) {
        if (entity.isCrouching() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
            matrices.mulPose(Axis.XP.rotation(model.body.xRot));
        }

        matrices.mulPose(Axis.YP.rotation(model.body.yRot));
        matrices.translate(0.0F, 0.4F, -0.16F);
    }

    static <T extends LivingEntity, M extends HumanoidModel<T>> void translateToRightArm(PoseStack matrices, M model, T entity) {
        if (entity.isCrouching() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
        }

        matrices.mulPose(Axis.YP.rotation(model.body.yRot));
        matrices.translate(-0.3125F, 0.15625F, 0.0F);
        matrices.mulPose(Axis.ZP.rotation(model.rightArm.zRot));
        matrices.mulPose(Axis.YP.rotation(model.rightArm.yRot));
        matrices.mulPose(Axis.XP.rotation(model.rightArm.xRot));
        matrices.translate(-0.0625F, 0.625F, 0.0F);
    }

    static <T extends LivingEntity, M extends HumanoidModel<T>> void translateToLeftArm(PoseStack matrices, M model, T entity) {
        if (entity.isCrouching() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.2F, 0.0F);
        }

        matrices.mulPose(Axis.YP.rotation(model.body.yRot));
        matrices.translate(0.3125F, 0.15625F, 0.0F);
        matrices.mulPose(Axis.ZP.rotation(model.leftArm.zRot));
        matrices.mulPose(Axis.YP.rotation(model.leftArm.yRot));
        matrices.mulPose(Axis.XP.rotation(model.leftArm.xRot));
        matrices.translate(0.0625F, 0.625F, 0.0F);
    }

    static <T extends LivingEntity, M extends HumanoidModel<T>> void translateToRightLeg(PoseStack matrices, M model, T entity) {
        if (entity.isCrouching() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.0F, 0.25F);
        }

        matrices.translate(-0.125F, 0.75F, 0.0F);
        matrices.mulPose(Axis.ZP.rotation(model.rightLeg.zRot));
        matrices.mulPose(Axis.YP.rotation(model.rightLeg.yRot));
        matrices.mulPose(Axis.XP.rotation(model.rightLeg.xRot));
        matrices.translate(0.0F, 0.75F, 0.0F);
    }

    static <T extends LivingEntity, M extends HumanoidModel<T>> void translateToLeftLeg(PoseStack matrices, M model, T entity) {
        if (entity.isCrouching() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.0F, 0.25F);
        }

        matrices.translate(0.125F, 0.75F, 0.0F);
        matrices.mulPose(Axis.ZP.rotation(model.leftLeg.zRot));
        matrices.mulPose(Axis.YP.rotation(model.leftLeg.yRot));
        matrices.mulPose(Axis.XP.rotation(model.leftLeg.xRot));
        matrices.translate(0.0F, 0.75F, 0.0F);
    }
}
