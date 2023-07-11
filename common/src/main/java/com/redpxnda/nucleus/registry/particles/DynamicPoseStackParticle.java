package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.redpxnda.nucleus.util.RenderUtil.*;

public class DynamicPoseStackParticle extends DynamicParticle {
    protected RenderType renderType;
    protected final TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> renderHandler;

    protected DynamicPoseStackParticle(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> onRender, TextureAtlasSprite sprite, RenderType renderType, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(onSetup, onTick, (a, b) -> {}, clientLevel, x, y, z, dx, dy, dz);
        this.renderHandler = onRender;
        this.renderType = renderType;
        this.sprite = sprite;
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
        poseStack.pushPose();
        renderHandler.accept(this, poseStack, camera);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vc = bufferSource.getBuffer(renderType);
        addDoubleQuad(poseStack, vc, red, green, blue, alpha, scale/2f, scale/2f, 0, 0, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), LightTexture.FULL_BRIGHT);
        poseStack.popPose();
        bufferSource.endBatch();
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class Provider extends DynamicParticle.Provider {
        protected TextureAtlasSprite sprite;
        protected final ResourceLocation location;
        protected final RenderType renderType;
        protected final TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> renderHandler;

        public Provider(ResourceLocation spriteLocation, RenderType renderType, Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, TriConsumer<DynamicPoseStackParticle, PoseStack, Camera> renderHandler) {
            super(onSetup, onTick, (a, b) -> {});
            this.location = spriteLocation;
            this.renderType = renderType;
            this.renderHandler = renderHandler;
        }

        protected void setupSprite() {
            this.sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(location);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            if (sprite == null) setupSprite();
            return new DynamicPoseStackParticle(onSetup, onTick, renderHandler, sprite, renderType, clientLevel, d, e, f, g, h, i);
        }
    }
}
