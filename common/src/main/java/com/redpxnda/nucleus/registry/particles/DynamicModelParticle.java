package com.redpxnda.nucleus.registry.particles;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;

public class DynamicModelParticle extends DynamicPoseStackParticle {
    protected final Model model;
    protected final RenderLayer renderType;

    protected DynamicModelParticle(Model model, RenderLayer renderType, ClientWorld clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(null, renderType, clientLevel, x, y, z, dx, dy, dz);
        this.model = model; // new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK)); //new GuardianModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN));
        this.renderType = renderType;
        this.maxAge = 100;
        this.gravityStrength = 0f;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, MatrixStack poseStack, float x, float y, float z, Camera camera, float partialTick) {
        super.render(vertexConsumer, poseStack, x, y, z, camera, partialTick);
        this.model.render(poseStack, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
    }
}
