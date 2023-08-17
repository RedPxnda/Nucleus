package com.redpxnda.nucleus.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;

@Environment(EnvType.CLIENT)
public record ArmRenderer(RenderHandler handler, PoseStack poseStack, MultiBufferSource bufferSource, int light, float equippedProgress, float swingProgress, HumanoidArm side) {

    public void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, float equippedProgress, float swingProgress, HumanoidArm side) {
        handler.renderPlayerArm(poseStack, buffer, combinedLight, equippedProgress, swingProgress, side);
    }
    public void renderPlayerArm() {
        renderPlayerArm(poseStack, bufferSource, light, equippedProgress, swingProgress, side);
    }
    public void renderPlayerArm(HumanoidArm side) {
        renderPlayerArm(poseStack, bufferSource, light, equippedProgress, swingProgress, side);
    }
    public void renderBothPlayerArms() {
        poseStack.pushPose();
        renderPlayerArm(HumanoidArm.LEFT);
        poseStack.popPose();

        poseStack.pushPose();
        renderPlayerArm(HumanoidArm.RIGHT);
        poseStack.popPose();
    }

    public interface RenderHandler {
        void renderPlayerArm(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, float equippedProgress, float swingProgress, HumanoidArm side);
    }
}
