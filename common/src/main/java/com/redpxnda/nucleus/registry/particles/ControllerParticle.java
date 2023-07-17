package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ControllerParticle extends DynamicPoseStackParticle {
    public int spawnDelay = 0;
    public int loops = 0;
    public List<DynamicPoseStackParticle> children = new ArrayList<>();
    public Consumer<ControllerParticle> loopFunction = p -> {};

    protected ControllerParticle(PoseStack poseStack, RenderType renderType, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(poseStack, renderType, clientLevel, x, y, z, dx, dy, dz);
    }

    public void updateChildren() {
        updateChildren(true);
    }

    public void updateChildren(boolean initial) {
        this.children.forEach(c -> {
            c.px = px+x;
            c.py = py+y;
            c.pz = pz+z;
            if (initial) {
                c.poseStack = this.poseStack;
                c.updateLifetime();
            }
            if (c instanceof ControllerParticle cp)
                cp.updateChildren();
        });
    }

    @Override
    public void render(VertexConsumer vc, float x, float y, float z, Camera camera, float pt) {
        if (poseStack == null) return;
        renderBeforePush(vc, poseStack, x, y, z, camera, pt);
        poseStack.pushPose();
        render(vc, poseStack, x, y, z, camera, pt);
        poseStack.popPose();
        renderAfterPop(vc, poseStack, x, y, z, camera, pt);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, PoseStack poseStack, float x, float y, float z, Camera camera, float pt) {
        if (spawnDelay > 0) return;
        poseStack.translate(x, y, z);
        children.forEach(c -> {
            if (c.isAlive()) {
                this.x = this.x;
                float cx = (float) (Mth.lerp(pt, c.getXO(), c.getX()));
                float cy = (float) (Mth.lerp(pt, c.getYO(), c.getY()));
                float cz = (float) (Mth.lerp(pt, c.getZO(), c.getZ()));
                poseStack.pushPose();
                if (c.disconnected) {
                    poseStack.translate(-x, -y, -z);
                    poseStack.translate(c.discX, c.discY, c.discZ);
                }
                c.render(vertexConsumer, cx, cy, cz, camera, pt);
                poseStack.popPose();
            }
        });
    }

    @Override
    public void tick() {
        if (spawnDelay > 0) {
            spawnDelay--;
            return;
        }
        if (age >= lifetime && loops > 0) {
            age = 0;
            loops--;
            loopFunction.accept(this);
        }
        children.forEach(child -> {
            if (child.isAlive()) {
                if (!child.disconnected || Double.isNaN(child.discX) || Double.isNaN(child.discY) || Double.isNaN(child.discZ)) {
                    child.px = px + x;
                    child.py = py + y;
                    child.pz = pz + z;
                    if (child.disconnected) {
                        child.discX = x;
                        child.discY = y;
                        child.discZ = z;
                    }
                }
                child.tick();
            }
        });
        super.tick();
    }

    @Override
    public void remove() {
        super.remove();
        children.forEach(child -> child.remove());
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new ControllerParticle(new PoseStack(), RenderType.translucent(), clientLevel, d, e, f, g, h, i);
        }
    }
}
