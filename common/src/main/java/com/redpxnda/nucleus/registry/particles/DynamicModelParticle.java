package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicModelParticle extends DynamicPoseStackParticle {
    protected final Model model;
    protected final RenderType renderType;

    protected DynamicModelParticle(Model model, RenderType renderType, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(null, renderType, clientLevel, x, y, z, dx, dy, dz);
        this.model = model; // new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK)); //new GuardianModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN));
        this.renderType = renderType;
        this.lifetime = 100;
        this.gravity = 0f;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, PoseStack poseStack, float x, float y, float z, Camera camera, float partialTick) {
        super.render(vertexConsumer, poseStack, x, y, z, camera, partialTick);
        this.model.renderToBuffer(poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }
}
