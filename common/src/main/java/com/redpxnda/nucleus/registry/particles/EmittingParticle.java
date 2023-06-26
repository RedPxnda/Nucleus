package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EmittingParticle extends Particle {
    private final ParticleOptions emit;
    private final Supplier<Double> frequency;
    private final Supplier<Double> count;
    private final Supplier<Double> speed;
    private long lastSpawn = -100;
    private int startingTicks;

    public EmittingParticle(
            int lifetime, ParticleOptions emit, Supplier<Double> frequency, Supplier<Double> count, Supplier<Double> speed, float grav, float fric, boolean physics, int startingTick,
            ClientLevel clientLevel, double d, double e, double f, double g, double h, double i
    ) {
        super(clientLevel, d, e, f);
        this.setParticleSpeed(g, h, i);
        this.lifetime = lifetime;
        this.emit = emit;
        this.frequency = frequency;
        this.count = count;
        this.speed = speed;
        this.gravity = grav;
        this.friction = fric;
        this.hasPhysics = physics;
        this.startingTicks = startingTick;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        if (startingTicks > 0) {
            startingTicks--;
            return;
        }
        if (level.getGameTime()-frequency.get() > lastSpawn) {
            for (int i = 0; i < count.get(); i++) {
                level.addParticle(emit, x, y, z, speed.get(), speed.get(), speed.get());
            }
            lastSpawn = level.getGameTime();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<EmittingParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(EmittingParticleOptions o, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new EmittingParticle(o.lifetime(), o.emit(), o.freq(), o.count(), o.speed(), o.gravity(), o.friction(), o.physics(), o.start(), clientLevel, d, e, f, g, h, i);
        }
    }
}
