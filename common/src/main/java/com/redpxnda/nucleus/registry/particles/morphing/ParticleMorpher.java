package com.redpxnda.nucleus.registry.particles.morphing;

import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.registry.particles.ControllerParticle;
import com.redpxnda.nucleus.registry.particles.DynamicPoseStackParticle;
import com.redpxnda.nucleus.client.Rendering;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.ArrayList;
import java.util.List;

public class ParticleMorpher extends ParticleShape {
    private final ClientLevel level;
    private final List<ControllerParticle> controllers = new ArrayList<>();

    public ParticleMorpher(ClientLevel level, double x, double y, double z) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(ParticleShape shape) {
        controllers.add(shape.create(level));
    }

    public void spawn() {
        ControllerParticle cp = (ControllerParticle) Rendering.addParticleToWorld(
                level, NucleusRegistries.controllerParticle.get(), true, true,
                x, y, z, 0, 0, 0
        );

        cp.lifetimeMarginMax = additionalLifetime.max;
        cp.lifetimeMarginMin = additionalLifetime.min;
        cp.expectedLifetime = expectedLifetime;
        cp.setFriction(friction.get());
        cp.setGravity(gravity.get());
        cp.setRed(red.get());
        cp.setGreen(green.get());
        cp.setBlue(blue.get());
        cp.setAlpha(alpha.get());
        cp.setScale(scale.get());
        cp.setPhysicsEnabled(physics);
        cp.motionFunction = motionFunction;
        cp.loops = loops;
        cp.spawnDelay = delay;
        cp.updateLifetime();

        cp.children.addAll(controllers.stream().map(p -> (DynamicPoseStackParticle) p).toList());
        cp.updateChildren();
    }
}
