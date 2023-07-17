package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Supplier;

public class EmitterParticle extends DynamicPoseStackParticle {
    private ParticleOptions emit;
    private Supplier<Double> frequency;
    private Supplier<Double> count;
    private Supplier<Double> speed;
    private long lastSpawn = -100;
    private int startingTicks;

    public EmitterParticle(EmitterParticleOptions options, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
        super(null, RenderType.translucent(), clientLevel, d, e, f, g, h, i);
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
    public void render(VertexConsumer vertexConsumer, PoseStack poseStack, float x, float y, float z, Camera camera, float f) {
        if (startingTicks > 0) {
            startingTicks--;
            return;
        }
        if (level.getGameTime()-frequency.get() > lastSpawn) {
            super.render(vertexConsumer, poseStack, x, y, z, camera, f);

            for (int i = 0; i < count.get(); i++) {
                level.addParticle(emit, x, y, z, speed.get(), speed.get(), speed.get());
            }
            lastSpawn = level.getGameTime();
        }
    }

    public static class Provider implements ParticleProvider<EmitterParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(EmitterParticleOptions o, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new EmitterParticle(o, clientLevel, d, e, f, g, h, i);
        }
    }
}
