package com.redpxnda.nucleus.registry.particles.morphing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.registry.NucleusRegistries;
import com.redpxnda.nucleus.registry.particles.ControllerParticle;
import com.redpxnda.nucleus.registry.particles.DynamicPoseStackParticle;
import com.redpxnda.nucleus.registry.particles.manager.PoseStackParticleManager;
import com.redpxnda.nucleus.util.RenderUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParticleShape {
    private final Set<String> setValues = new HashSet<>();
    private final List<ParticleShape> children = new ArrayList<>();
    public double x = 0, y = 0, z = 0;
    public RandomInt additionalLifetime = new RandomInt(0, 0);
    public int expectedLifetime = 100;
    public RandomFloat friction = new RandomFloat(0.98f);
    public RandomFloat gravity = new RandomFloat(0);
    public RandomFloat red = new RandomFloat(1);
    public RandomFloat green = new RandomFloat(1);
    public RandomFloat blue = new RandomFloat(1);
    public RandomFloat alpha = new RandomFloat(1);
    public RandomFloat scale = new RandomFloat(1);
    public boolean physics = false;
    public int loops = 0;
    public int delay = 0;
    public MotionFunction motionFunction = (a, b, c) -> b;
    public MotionFunction outerMotionFunction = (a, b, c) -> b;
    public SpawnFunction spawnFunction = SpawnFunction.SINGLE;
    public TickerFunction tickerFunction = m -> {};
    public TickerFunction outerTickerFunction = m -> {};
    public AnimationFunction animationFunction = (ps, a) -> {};
    public ParticleOptions particle;

    public ParticleShape() {
    }
    public ParticleShape(ParticleShape other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.additionalLifetime = other.additionalLifetime.copy();
        this.expectedLifetime = other.expectedLifetime;
        this.friction = other.friction.copy();
        this.gravity = other.gravity.copy();
        this.red = other.red.copy();
        this.green = other.green.copy();
        this.blue = other.blue.copy();
        this.alpha = other.alpha.copy();
        this.scale = other.scale.copy();
        this.physics = other.physics;
        this.loops = other.loops;
        this.delay = other.delay;
        this.motionFunction = other.motionFunction;
        this.spawnFunction = other.spawnFunction;
        this.tickerFunction = other.tickerFunction;
        this.animationFunction = other.animationFunction;
        this.particle = other.particle;
    }

    public ParticleShape(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void addChild(ParticleShape shape) {
        children.add(shape);
    }

    public void setParticle(ParticleOptions particle) {
        this.particle = particle;
    }

    public void setLifetime(int lifetime) {
        expectedLifetime = lifetime;
        setValues.add("lifetime");
    }

    public void setLifetimeMargin(int min, int max) {
        additionalLifetime = new RandomInt(min, max);
        setValues.add("lifetime");
    }

    public void setFriction(float fric) {
        friction.set(fric);
        setValues.add("friction");
    }
    public void setFriction(RandomFloat fric) {
        friction = fric;
        setValues.add("friction");
    }

    public void setGravity(float grav) {
        gravity.set(grav);
        setValues.add("gravity");
    }
    public void setGravity(RandomFloat grav) {
        gravity.set(grav);
        setValues.add("gravity");
    }

    public void setColor(float r, float g, float b, float a) {
        red.set(r);
        green.set(g);
        blue.set(b);
        alpha.set(a);
        setValues.add("color");
    }
    public void setColor(RandomFloat r, RandomFloat g, RandomFloat b, RandomFloat a) {
        red.set(r);
        green.set(g);
        blue.set(b);
        alpha.set(a);
        setValues.add("color");
    }

    public void setScale(float sc) {
        scale.set(sc);
        setValues.add("scale");
    }
    public void setScale(RandomFloat sc) {
        scale.set(sc);
        setValues.add("scale");
    }

    public void setPhysicsEnabled(boolean physics) {
        this.physics = physics;
        setValues.add("physics");
    }

    public void setMotionFunction(MotionFunction function) {
        motionFunction = function;
    }
    public void setOuterMotionFunction(MotionFunction function) {
        outerMotionFunction = function;
    }

    public void setSpawnFunction(SpawnFunction function) {
        spawnFunction = function;
    }

    public void setTickerFunction(TickerFunction function) {
        tickerFunction = function;
    }
    public void setOuterTickerFunction(TickerFunction function) {
        outerTickerFunction = function;
    }

    public void setAnimationFunction(AnimationFunction function) {
        animationFunction = function;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    protected ControllerParticle create(ClientLevel level) {
        ControllerParticle controller = (ControllerParticle) RenderUtil.createParticle(
                level, NucleusRegistries.controllerParticle.get(),
                x, y, z, 0, 0, 0);

        if (controller == null) {
            Nucleus.LOGGER.error("Failed to create ControllerParticle for ParticleShape!");
            throw new AssertionError();
        }

        applyTo(controller);
        controller.loopFunction = this::applyTo;

        return controller;
    }

    // value is set
    protected boolean VIS(String v) {
        return setValues.contains(v);
    }

    protected void applyTo(ControllerParticle controller) {
        ClientLevel level = controller.getLevel();
        controller.children.clear();
        controller.setPos(x, y, z);
        Spawner spawner = (px, py, pz) -> {
            DynamicPoseStackParticle particle = (DynamicPoseStackParticle) RenderUtil.createParticle(
                    level, this.particle,
                    px, py, pz, 0, 0, 0
            );
            assert particle != null;

            if (VIS("lifetime")) particle.lifetimeMarginMax = additionalLifetime.max;
            if (VIS("lifetime")) particle.lifetimeMarginMin = additionalLifetime.min;
            if (VIS("lifetime")) particle.expectedLifetime = expectedLifetime;
            if (VIS("friction")) particle.setFriction(friction.get());
            if (VIS("gravity")) particle.setGravity(gravity.get());
            if (VIS("color")) {
                particle.setRed(red.get());
                particle.setGreen(green.get());
                particle.setBlue(blue.get());
                particle.setAlpha(alpha.get());
            }
            if (VIS("scale")) particle.setScale(scale.get());
            if (VIS("physics")) particle.setPhysicsEnabled(physics);
            particle.motionFunction = motionFunction;
            particle.animationFunction = animationFunction;
            particle.tickerFunction = tickerFunction;

            controller.children.add(particle);
        };
        spawnFunction.call(spawner);

        controller.expectedLifetime = expectedLifetime+additionalLifetime.max;
        controller.loops = loops;
        controller.spawnDelay = delay;
        controller.tickerFunction = outerTickerFunction;
        controller.motionFunction = outerMotionFunction;

        children.forEach(child -> controller.children.add(child.create(level)));

        //controller.updateChildren();
    }

    public static class RandomInt {
        public int min;
        public int max;

        public RandomInt(int expected) {
            this.min = expected;
            this.max = expected;
        }
        public RandomInt(int min, int max) {
            this.min = min;
            this.max = max;
        }
        public RandomInt copy() {
            return new RandomInt(min, max);
        }

        public void set(int val) {
            this.min = val;
            this.max = val;
        }
        public int get() {
            if (min == max) return min;
            return MathUtil.random(min, max);
        }
    }
    public static class RandomFloat {
        public float min;
        public float max;

        public RandomFloat(float expected) {
            this.min = expected;
            this.max = expected;
        }
        public RandomFloat(float min, float max) {
            this.min = min;
            this.max = max;
        }
        public RandomFloat copy() {
            return new RandomFloat(min, max);
        }

        public void set(float val) {
            this.min = val;
            this.max = val;
        }
        public void set(RandomFloat other) {
            this.min = other.min;
            this.max = other.max;
        }
        public float get() {
            if (min == max) return min;
            return (float) MathUtil.random(min, max);
        }
    }
    public interface MotionFunction {
        Vec3 move(Vec3 pos, Vec3 motion, Double age);
    }
    public interface AnimationFunction {
        void animate(Matrix4f matrix, float age);
    }
    public interface SpawnFunction {
        SpawnFunction SINGLE = spawner -> spawner.spawn(0, 0, 0);
        static SpawnFunction CIRCLE(double increment, double radius) {
            return spawner -> {
                for (double i = 0; i < 360; i+=increment) {
                    spawner.spawn(Math.cos(Math.toRadians(i))*radius, 0, Math.sin(Math.toRadians(i))*radius);
                }
            };
        }

        void call(Spawner spawner);
    }
    public interface Spawner {
        void spawn(double x, double y, double z);
    }
    public interface TickerFunction {
        void animate(PoseStackParticleManager manager);
    }
}
