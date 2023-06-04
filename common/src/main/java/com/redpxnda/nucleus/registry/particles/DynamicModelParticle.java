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
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicModelParticle extends DynamicParticle {
    protected final Model model;
    protected final RenderType renderType;
    protected final TriConsumer<DynamicModelParticle, PoseStack, Camera> renderHandler;

    protected DynamicModelParticle(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, TriConsumer<DynamicModelParticle, PoseStack, Camera> onRender, Model model, RenderType renderType, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(onSetup, onTick, (a, b) -> {}, clientLevel, x, y, z, dx, dy, dz);
        this.renderHandler = onRender;
        this.model = model; // new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK)); //new GuardianModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN));
        this.renderType = renderType;
        this.lifetime = 100;
        this.gravity = 0f;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(xo - vec3.x());
        float y = (float)(yo - vec3.y());
        float z = (float)(zo - vec3.z());
        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, z);
        poseStack.scale(1, -1, 1);
        renderHandler.accept(this, poseStack, camera);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vc = bufferSource.getBuffer(renderType);
        this.model.renderToBuffer(poseStack, vc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
        bufferSource.endBatch();
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider extends DynamicParticle.Provider {
        protected final Supplier<Model> model;
        protected final RenderType renderType;
        protected final TriConsumer<DynamicModelParticle, PoseStack, Camera> renderHandler;

        public Provider(Supplier<Model> model, RenderType renderType, Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, TriConsumer<DynamicModelParticle, PoseStack, Camera> renderHandler) {
            super(onSetup, onTick, (a, b) -> {});
            this.model = model;
            this.renderType = renderType;
            this.renderHandler = renderHandler;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new DynamicModelParticle(onSetup, onTick, renderHandler, model.get(), renderType, clientLevel, d, e, f, g, h, i);
        }
    }
}
