package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.registry.particles.manager.PoseStackParticleManager;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import com.redpxnda.nucleus.util.LimitedArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public abstract class DynamicPoseStackParticle extends DynamicParticle implements PoseStackParticleManager {
    protected RenderLayer renderType;
    public MatrixStack poseStack;
    public boolean disconnected = false;
    public double discX = Double.NaN;
    public double discY = Double.NaN;
    public double discZ = Double.NaN;
    public double py;
    public double pz;
    public double px;
    public ParticleShape.AnimationFunction animationFunction = (s, m, a) -> {};
    public ParticleShape.TickerFunction tickerFunction = p -> {};
    protected final Matrix4f newMatrix = new Matrix4f();
    protected final Matrix4f oldMatrix = new Matrix4f();
    public List<Vector3f> pastPositions = null;
    public Vector3f oldestPosition = new Vector3f();
    public Trail trail = null;

    protected DynamicPoseStackParticle(MatrixStack poseStack, RenderLayer renderType, ClientWorld clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(clientLevel, x, y, z, dx, dy, dz);
        this.renderType = renderType;
        this.poseStack = poseStack;
    }

    @Override
    public void render(VertexConsumer vc, float x, float y, float z, Camera camera, float pt) {
        if (poseStack == null) {
            poseStack = new MatrixStack();
        }

        VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        vc = bufferSource.getBuffer(renderType);

        renderBeforePush(vc, poseStack, x, y, z, camera, pt);
        poseStack.push();
        render(vc, poseStack, x, y, z, camera, pt);
        poseStack.pop();
        renderAfterPop(vc, poseStack, x, y, z, camera, pt);
        bufferSource.draw();
    }

    public void renderTrail(MatrixStack stack, float pt) {
        if (trail == null || pastPositions == null || pastPositions.isEmpty()) return;
        VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vc = bufferSource.getBuffer(Rendering.transparentTriangleStrip);
        int length = pastPositions.size();
        float width = trail.width/length;
        for (int i = 0; i < length; i++) {
            Vector3f past = i == 0 ? oldestPosition : pastPositions.get(i-1);
            Vector3f current = pastPositions.get(i);
            Vector3f next = i == length-1 ? new Vector3f((float) x, (float) y, (float) z) : pastPositions.get(i+1);

            next = new Vector3f(current).lerp(next, pt);
            current = new Vector3f(past).lerp(current, pt);

            float offset = width*(i);
            float nextOffset = width*(i+1);

            int light = trail.emissive ? LightmapTextureManager.MAX_LIGHT_COORDINATE : LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE;

            vc.vertex(stack.peek().getPositionMatrix(), current.x, current.y+offset, current.z).color(trail.red, trail.green, trail.blue, 0f).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), current.x, current.y-offset, current.z).color(trail.red, trail.green, trail.blue, 0f).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), next.x, next.y-nextOffset, next.z).color(trail.red, trail.green, trail.blue, 0f).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), current.x, current.y+offset, current.z).color(trail.red, trail.green, trail.blue, trail.alpha).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), next.x, next.y-nextOffset, next.z).color(trail.red, trail.green, trail.blue, trail.alpha).light(light).next();
            vc.vertex(stack.peek().getPositionMatrix(), next.x, next.y+nextOffset, next.z).color(trail.red, trail.green, trail.blue, trail.alpha).light(light).next();
        }
    }

    public void render(VertexConsumer vc, MatrixStack stack, float x, float y, float z, Camera camera, float partialTick) {
        stack.translate(x, y, z);

        float scale = MathHelper.lerp(partialTick, oldScale, this.scale);
        poseStack.scale(scale/2f, scale/2f, scale/2f);

        poseStack.multiplyPositionMatrix(oldMatrix.lerp(newMatrix, partialTick));
    }
    public void renderBeforePush(VertexConsumer vc, MatrixStack stack, float x, float y, float z, Camera camera, float partialTick) {
    }
    public void renderAfterPop(VertexConsumer vc, MatrixStack stack, float x, float y, float z, Camera camera, float partialTick) {
    }

    @Override
    public void tick() {
        if (pastPositions == null) pastPositions = new LimitedArrayList<>(trail == null ? 5 : trail.maxLength);
        if (trail == null || world.getTime() % trail.saveInterval == 0) {
            if (pastPositions.size() >= 1) oldestPosition.set(pastPositions.get(0));
            pastPositions.add(new Vector3f((float) x, (float) y, (float) z));
        }
        oldMatrix.set(newMatrix);
        animationFunction.animate(newMatrix, this, getAge() / (float) _getLifetime());
        tickerFunction.animate(this);
        super.tick();
    }

    public void move(double d, double e, double f) {
        if (this.stopped) {
            return;
        }
        double g = d;
        double h = e;
        double i = f;
        if (this.collidesWithWorld && (d != 0.0 || e != 0.0 || f != 0.0) && d * d + e * e + f * f < MAX_SQUARED_COLLISION_CHECK_DISTANCE) {
            Vec3d vec3 = Entity.adjustMovementForCollisions(null, new Vec3d(d, e, f), createRelativeBB(), this.world, List.of());
            d = vec3.x;
            e = vec3.y;
            f = vec3.z;
        }
        if (d != 0.0 || e != 0.0 || f != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
            this.repositionFromBoundingBox();
        }
        if (Math.abs(h) >= (double)1.0E-5f && Math.abs(e) < (double)1.0E-5f) {
            this.stopped = true;
        }
        boolean bl = this.onGround = h != e && h < 0.0;
        if (g != d) {
            this.velocityX = 0.0;
        }
        if (i != f) {
            this.velocityZ = 0.0;
        }
    }

    public Box createRelativeBB() {
        float g = this.spacingXZ / 2.0f;
        float h = this.spacingY;
        return new Box(px+x - g, py+y, pz+z - g, px+x + g, py+y + h, pz+z + g);
    }

    @Override
    @NotNull
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    //@Override
    public MatrixStack getPoseStack() {
        return poseStack;
    }

    @Override
    public RenderLayer _getRenderType() {
        return renderType;
    }

    @Override
    public void setRenderType(RenderLayer rt) {
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

    @Override
    public Trail getTrail() {
        return trail;
    }

    @Override
    public void setTrail(Trail trail) {
        this.trail = trail;
    }

    @Override
    public void clearSavedPositions() {
        pastPositions.clear();
        oldestPosition = new Vector3f();
    }
}
