package com.redpxnda.nucleus.registry.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EmitterParticle extends DynamicPoseStackParticle {
    private ParticleEffect emit;
    private Supplier<Double> frequency;
    private Supplier<Double> count;
    private Supplier<Double> speed;
    private long lastSpawn = -100;
    private int startingTicks;

    public EmitterParticle(EmitterParticleOptions options, ClientWorld clientLevel, double d, double e, double f, double g, double h, double i) {
        super(null, RenderLayer.getTranslucent(), clientLevel, d, e, f, g, h, i);
        this.applyOptions(options);
    }

    @Override
    public void applyOptions(DynamicParticleOptions options) {
        super.applyOptions(options);
        if (options instanceof EmitterParticleOptions em) {
            emit = em.emit;
            frequency = em.frequency;
            count = em.count;
            speed = em.speed;
            startingTicks = em.startAfter;
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, MatrixStack poseStack, float x, float y, float z, Camera camera, float f) {
        if (startingTicks > 0) {
            startingTicks--;
            return;
        }
        if (world.getTime()-frequency.get() > lastSpawn) {
            super.render(vertexConsumer, poseStack, x, y, z, camera, f);

            for (int i = 0; i < count.get(); i++) {
                world.addParticle(emit, x, y, z, speed.get(), speed.get(), speed.get());
            }
            lastSpawn = world.getTime();
        }
    }

    public static class Provider implements ParticleFactory<EmitterParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(EmitterParticleOptions o, ClientWorld clientLevel, double d, double e, double f, double g, double h, double i) {
            return new EmitterParticle(o, clientLevel, d, e, f, g, h, i);
        }
    }
}
