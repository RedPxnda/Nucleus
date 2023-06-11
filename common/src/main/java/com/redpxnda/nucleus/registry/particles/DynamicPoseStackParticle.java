package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.redpxnda.nucleus.util.RenderUtil.*;

public class DynamicPoseStackParticle extends DynamicParticle {
    protected final RenderType renderType;
    protected final TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> renderHandler;

    protected DynamicPoseStackParticle(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> onRender, SpriteSet set, RenderType renderType, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(onSetup, onTick, (a, b) -> {}, set, clientLevel, x, y, z, dx, dy, dz);
        this.renderHandler = onRender;
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
        addDoubleQuad(poseStack, vc, red, green, blue, alpha, 0, 0, 0, 0, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV0(), LightTexture.FULL_BRIGHT);
        bufferSource.endBatch();
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider extends DynamicParticle.Provider {
        protected final RenderType renderType;
        protected final TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> renderHandler;

        public Provider(SpriteSet set, RenderType renderType, Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> renderHandler) {
            super(set, onSetup, onTick, (a, b) -> {});
            this.renderType = renderType;
            this.renderHandler = renderHandler;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new DynamicPoseStackParticle(onSetup, onTick, renderHandler, set, renderType, clientLevel, d, e, f, g, h, i);
        }
    }
}
