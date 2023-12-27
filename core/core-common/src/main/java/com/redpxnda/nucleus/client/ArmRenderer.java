package com.redpxnda.nucleus.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public record ArmRenderer(RenderHandler handler, MatrixStack poseStack, VertexConsumerProvider bufferSource, int light, float equippedProgress, float swingProgress, Arm side) {

    public void renderPlayerArm(MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, float equippedProgress, float swingProgress, Arm side) {
        handler.renderPlayerArm(poseStack, buffer, combinedLight, equippedProgress, swingProgress, side);
    }
    public void renderPlayerArm() {
        renderPlayerArm(poseStack, bufferSource, light, equippedProgress, swingProgress, side);
    }
    public void renderPlayerArm(Arm side) {
        renderPlayerArm(poseStack, bufferSource, light, equippedProgress, swingProgress, side);
    }
    public void renderBothPlayerArms() {
        poseStack.push();
        renderPlayerArm(Arm.LEFT);
        poseStack.pop();

        poseStack.push();
        renderPlayerArm(Arm.RIGHT);
        poseStack.pop();
    }

    public interface RenderHandler {
        void renderPlayerArm(MatrixStack poseStack, VertexConsumerProvider buffer, int combinedLight, float equippedProgress, float swingProgress, Arm side);
    }
}
