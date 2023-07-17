package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redpxnda.nucleus.registry.particles.manager.PoseStackParticleManager;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public abstract class DynamicPoseStackParticle extends DynamicParticle implements PoseStackParticleManager {
    protected RenderType renderType;
    public PoseStack poseStack;
    public boolean disconnected = false;
    public double discX = Double.NaN;
    public double discY = Double.NaN;
    public double discZ = Double.NaN;
    public double py;
    public double pz;
    public double px;
    public ParticleShape.AnimationFunction animationFunction = (s, a) -> {};
    public ParticleShape.TickerFunction tickerFunction = p -> {};
    private final Matrix4f newMatrix = new Matrix4f();
    private final Matrix4f oldMatrix = new Matrix4f();

    protected DynamicPoseStackParticle(PoseStack poseStack, RenderType renderType, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(clientLevel, x, y, z, dx, dy, dz);
        this.renderType = renderType;
        this.poseStack = poseStack;
    }

    @Override
    public void render(VertexConsumer vc, float x, float y, float z, Camera camera, float pt) {
        if (poseStack == null) {
            poseStack = new PoseStack();
        }
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        vc = bufferSource.getBuffer(renderType);
        renderBeforePush(vc, poseStack, x, y, z, camera, pt);
        poseStack.pushPose();
        render(vc, poseStack, x, y, z, camera, pt);
        poseStack.popPose();
        renderAfterPop(vc, poseStack, x, y, z, camera, pt);
        bufferSource.endBatch(renderType);
    }

    public void render(VertexConsumer vc, PoseStack stack, float x, float y, float z, Camera camera, float partialTick) {
        stack.translate(x, y, z);

        float scale = Mth.lerp(partialTick, oldScale, this.scale);
        poseStack.scale(scale/2f, scale/2f, scale/2f);

        poseStack.mulPoseMatrix(oldMatrix.lerp(newMatrix, partialTick));
    }
    public void renderBeforePush(VertexConsumer vc, PoseStack stack, float x, float y, float z, Camera camera, float partialTick) {
    }
    public void renderAfterPop(VertexConsumer vc, PoseStack stack, float x, float y, float z, Camera camera, float partialTick) {
    }

    @Override
    public void tick() {
        oldMatrix.set(newMatrix);
        animationFunction.animate(newMatrix, getAge() / (float) _getLifetime());
        tickerFunction.animate(this);
        super.tick();
    }

    public void move(double d, double e, double f) {
        if (this.stoppedByCollision) {
            return;
        }
        double g = d;
        double h = e;
        double i = f;
        if (this.hasPhysics && (d != 0.0 || e != 0.0 || f != 0.0) && d * d + e * e + f * f < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(d, e, f), createRelativeBB(), this.level, List.of());
            d = vec3.x;
            e = vec3.y;
            f = vec3.z;
        }
        if (d != 0.0 || e != 0.0 || f != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(d, e, f));
            this.setLocationFromBoundingbox();
        }
        if (Math.abs(h) >= (double)1.0E-5f && Math.abs(e) < (double)1.0E-5f) {
            this.stoppedByCollision = true;
        }
        boolean bl = this.onGround = h != e && h < 0.0;
        if (g != d) {
            this.xd = 0.0;
        }
        if (i != f) {
            this.zd = 0.0;
        }
    }

    public AABB createRelativeBB() {
        float g = this.bbWidth / 2.0f;
        float h = this.bbHeight;
        return new AABB(px+x - g, py+y, pz+z - g, px+x + g, py+y + h, pz+z + g);
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public PoseStack getPoseStack() {
        return poseStack;
    }

    @Override
    public RenderType _getRenderType() {
        return renderType;
    }

    @Override
    public void setRenderType(RenderType rt) {
        renderType = rt;
    }

    @Override
    public void disconnect() {
        disconnected = true;
    }

    @Override
    public void reconnect() {
        disconnected = false;
        discX = Double.NaN;
        discY = Double.NaN;
        discZ = Double.NaN;
    }
}
