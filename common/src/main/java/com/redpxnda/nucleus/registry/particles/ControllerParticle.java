package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import com.redpxnda.nucleus.util.LinkedArrayList;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ControllerParticle extends DynamicPoseStackParticle {
    public int spawnDelay = 0;
    public int loops = 0;
    public List<DynamicPoseStackParticle> children = new LinkedArrayList<>(this, (p, c) -> c.poseStack = p.poseStack);
    public List<ControllerParticle> queueToAdd = new ArrayList<>();
    public ParticleShape.LoopFunction loopFunction = c -> null;
    public int loopTime = -1;
    public ParticleShape shape;

    protected ControllerParticle(MatrixStack poseStack, RenderLayer renderType, ClientWorld clientLevel, double x, double y, double z, double dx, double dy, double dz) {
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
                cp.updateChildren(initial);
        });
    }

    @Override
    public void render(VertexConsumer vc, float x, float y, float z, Camera camera, float pt) {
        if (poseStack == null) return;
        renderBeforePush(vc, poseStack, x, y, z, camera, pt);
        poseStack.push();
        render(vc, poseStack, x, y, z, camera, pt);
        poseStack.pop();
        renderAfterPop(vc, poseStack, x, y, z, camera, pt);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, MatrixStack poseStack, float x, float y, float z, Camera camera, float pt) {
        if (spawnDelay > 0) return;
        poseStack.translate(x, y, z);
        poseStack.multiplyPositionMatrix(oldMatrix.lerp(newMatrix, pt));
        children.forEach(c -> {
            if (c.isAlive()) {
                float cx = (float) (MathHelper.lerp(pt, c.getXO(), c.getX()));
                float cy = (float) (MathHelper.lerp(pt, c.getYO(), c.getY()));
                float cz = (float) (MathHelper.lerp(pt, c.getZO(), c.getZ()));
                poseStack.push();
                if (c.disconnected) {
                    poseStack.translate(-x, -y, -z);
                    poseStack.translate(c.discX, c.discY, c.discZ);
                }
                c.renderTrail(poseStack, pt);
                c.render(vertexConsumer, cx, cy, cz, camera, pt);
                poseStack.pop();
            }
        });
    }

    @Override
    public void tick() {
        if (spawnDelay > 0) {
            spawnDelay--;
            return;
        }
        children.forEach(child -> {
            if (child.isAlive()) {
                if (child instanceof ControllerParticle cp) {
                    if (cp.spawnDelay <= 0 && cp.loops > 0) {
                        if (cp.loopTime > 0)
                            cp.loopTime--;
                        else if (cp.loopTime == 0) {
                            cp.loopTime = cp.shape.loopInterval;
                            cp.loops--;
                            ControllerParticle toAdd = cp.loopFunction.loop(cp);
                            if (toAdd != null) {
                                toAdd.px = cp.px;
                                toAdd.py = cp.py;
                                toAdd.pz = cp.pz;
                                toAdd.poseStack = poseStack;
                                toAdd.updateChildren();
                                queueToAdd.add(toAdd);
                            }
                        }
                    }
                }

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
        children.addAll(queueToAdd);
        queueToAdd.clear();
        children.removeIf(p -> !p.isAlive());
        super.tick();
    }

    @Override
    public void markDead() {
        super.markDead();
        children.forEach(child -> child.markDead());
    }

    public static class Provider implements ParticleFactory<DefaultParticleType> {

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType particleOptions, ClientWorld clientLevel, double d, double e, double f, double g, double h, double i) {
            return new ControllerParticle(new MatrixStack(), RenderLayer.getTranslucent(), clientLevel, d, e, f, g, h, i);
        }
    }
}
