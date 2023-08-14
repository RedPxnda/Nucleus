package com.redpxnda.nucleus.math;

import com.redpxnda.nucleus.network.clientbound.ParticleCreationPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ParticleShaper {
    public static ParticleShaper sphere(ParticleOptions options, double radius, double inc) {
        return new ParticleShaper(
                options,
                (s, i) -> {
                    double rads = i * Math.PI/180;
                    for (int j = 0; j < 360; j+=inc) {
                        Vector3d vec = new Vector3d(Math.sin(rads)*radius, 0, Math.cos(rads)*radius);
                        vec = vec.rotate(AxisD.ZP.rotationDegrees(j));
                        s.spawn(vec.x(), vec.y(), vec.z());
                    }
                }, 360, inc
        );
    }
    public static ParticleShaper expandingSphere(ParticleOptions options, double radius, double inc, double speed) {
        return new ParticleShaper(
                options,
                (s, i) -> {
                    double rads = i * Math.PI/180;
                    for (int j = 0; j < 360; j+=inc) {
                        Vector3d vec = new Vector3d(Math.sin(rads)*radius, 0, Math.cos(rads)*radius);
                        vec = vec.rotate(AxisD.ZP.rotationDegrees(j));
                        s.spawn(vec.x(), vec.y(), vec.z(), vec.x()*speed, vec.y()*speed, vec.z()*speed);
                    }
                }, 360, inc
        );
    }
    public static ParticleShaper circle(ParticleOptions options, double radius, double inc) {
        return new ParticleShaper(
                options,
                (s, i) -> {
                    double rads = i * Math.PI/180;
                    s.spawn(Math.sin(rads)*radius, Math.cos(rads)*radius);
                }, 360, inc
        );
    }
    public static ParticleShaper cylinder(ParticleOptions options, double radius, double inc, double startHeight, double maxHeight, double hInc) {
        return new ParticleShaper(
                options,
                (s, i) -> {
                    double rads = i * Math.PI/180;
                    double x = Math.sin(rads)*radius;
                    double z = Math.cos(rads)*radius;
                    for (double y = startHeight; y < maxHeight; y+=hInc) {
                        s.spawn(x, y, z);
                    }
                }, 360, inc
        );
    }
    public static ParticleShaper expandingCircle(ParticleOptions options, double radius, double inc, double speed) {
        return new ParticleShaper(
                options,
                (s, i) -> {
                    double rads = i * Math.PI/180;
                    s.spawn(Math.sin(rads)*radius, Math.cos(rads)*radius, Math.sin(rads)*speed, Math.cos(rads)*speed);
                }, 360, inc
        );
    }
    public static ParticleShaper square(ParticleOptions options, double r, int max, int inc) {
        double[][] setup = new double[][]{
                {0, 0, 0},
                {r, 0, 0},
                {r, 0, r},
                {0, 0, r},
        };
        return polygon(setup, options, r, max, inc);
    }
    public static ParticleShaper expandingSquare(ParticleOptions options, double r, int max, int inc, double speed) {
        double[][] setup = new double[][]{
                {0, 0},
                {r, 0},
                {r, r},
                {0, r},
        };
        return expandingPolygon(setup, options, r, max, inc, speed);
    }
    public static ParticleShaper bezier(double[][] controls, ParticleOptions options, double inc) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.bezier(controls, i);
            s.spawn(doubles[0], doubles[1], doubles[2]);
        }, 1, inc);
    }
    public static ParticleShaper polygon(double[][] shape, ParticleOptions options, double r, int max, int inc) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.arrayLerp3(i.intValue(), max, shape);
            s.spawn(doubles[0]-r/2, doubles[1]-r/2, doubles[2]-r/2);
        }, max, inc);
    }
    public static ParticleShaper expandingPolygon(double[][] shape, ParticleOptions options, double r, int max, int inc, double speed) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.arrayLerp3(i.intValue(), max, shape);
            s.spawn(doubles[0]-r/2, doubles[1]-r/2, doubles[2]-r/2, (doubles[0]-r/2)/r * speed, (doubles[1]-r/2)/r * speed, (doubles[2]-r/2)/r * speed);
        }, max, inc);
    }
    public static ParticleShaper polygon(double[][] shape, ParticleOptions options, int max, int inc) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.arrayLerp3(i.intValue(), max, shape);
            s.spawn(doubles[0], doubles[1], doubles[2]);
        }, max, inc);
    }
    public static ParticleShaper create(ParticleOptions particle, BiConsumer<ParticleShaper, Double> func, double max, double min, double inc) {
        return new ParticleShaper(particle, func, max, min, inc);
    }

    protected final double max;
    protected final double min;
    protected final double inc;
    protected double x;
    protected double y;
    protected double z;
    protected Quaterniond transformation;
    protected Quaterniond inheritT = new Quaterniond();
    protected Level level;
    protected ParticleSpawnMethod particleSpawner;
    protected final BiConsumer<ParticleShaper, Double> func;
    protected final ParticleOptions particle;
    protected Cacher cacher;
    protected Function<Vector3d, Vector3d> motion = v -> new Vector3d(0, 0, 0);

    public ParticleShaper(ParticleOptions particle, BiConsumer<ParticleShaper, Double> func, double max, double min, double inc) {
        this.max = max;
        this.min = min;
        this.inc = inc;
        this.func = func;
        this.particle = particle;
    }
    public ParticleShaper(ParticleOptions particle, BiConsumer<ParticleShaper, Double> func, double max, double inc) {
        this(particle, func, max, 0, inc);
    }
    public ParticleShaper(ParticleShaper o) {
        this(o.particle, o.func, o.max, o.min, o.inc);
        this.cacher = o.cacher;
    }
    public static ParticleShaper duplicate(ParticleShaper o) {
        ParticleShaper s = new ParticleShaper(o);
        s.x = o.x;
        s.y = o.y;
        s.z = o.z;
        s.transformation = o.transformation;
        s.cacher = o.cacher;
        s.level = o.level;
        s.particleSpawner = o.particleSpawner;
        return s;
    }

    public ParticleOptions particle() {
        return particle;
    }

    public void spawn(double x, double z) {
        spawn(x, 0, z);
    }
    public void spawn(double x, double z, double xs, double zs) {
        spawn(x, 0, z, xs, 0, zs);
    }
    public void spawn(double x, double y, double z) {
        Vector3d vec = new Vector3d(x, y, z);
        spawn(vec, motion.apply(vec));
    }
    public void spawn(double x, double y, double z, double xs, double ys, double zs) {
        spawn(new Vector3d(x, y, z), new Vector3d(xs, ys, zs));
    }
    public void spawn(Vector3d vec, Vector3d mot) {
        if (cacher != null && !cacher.has(this)) cacher.put(vec, mot);
        if (transformation != null) {
            vec = vec.rotate(transformation);
            mot = mot.rotate(transformation);
        }
        particleSpawner.spawn(level, particle(), vec.x+this.x, vec.y+this.y, vec.z+this.z, mot.x, mot.y, mot.z);
    }

    public ParticleShaper motion(Function<Vector3d, Vector3d> motion) {
        this.motion = motion;
        return this;
    }

    public ParticleShaper transform(Quaterniond transformation) {
        return transform(transformation, false);
    }
    public ParticleShaper transform(Quaterniond transformation, boolean inherit) {
        if (inherit) {
            this.inheritT = new Quaterniond(transformation);
            return this;
        }
        if (this.transformation != null)
            this.transformation.mul(transformation);
        else
            this.transformation = new Quaterniond(transformation);
        return this;
    }
    public Quaterniond getTransformation() {
        return transformation;
    }
    public Quaterniond getBaseTransformation() {
        return inheritT;
    }

    public ParticleShaper cacher(Cacher cacher) {
        this.cacher = cacher;
        return this;
    }
    public Cacher getCacher() {
        return cacher;
    }

    public ParticleShaper fromClient() {
        this.particleSpawner = Level::addParticle;
        return this;
    }
    public ParticleShaper fromServer() {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> new ParticleCreationPacket(op, x, y, z, xs, ys, zs).send((ServerLevel) l);
        return this;
    }
    public ParticleShaper fromServer(int count, double maxSpeed) {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> ((ServerLevel) l).sendParticles(op, x, y, z, count, xs, ys, zs, maxSpeed);
        return this;
    }
    public ParticleShaper fromServer(ServerPlayer player) {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> new ParticleCreationPacket(op, x, y, z, xs, ys, zs).send(player);
        return this;
    }
    public ParticleShaper fromServer(ServerPlayer player, int count, int maxSpeed, boolean overrideLimiter) {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> ((ServerLevel) l).sendParticles(player, op, overrideLimiter, x, y, z, count, xs, ys, zs, maxSpeed);
        return this;
    }

    public void runAt(Level level, double x, double y, double z) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;

        transform(inheritT);
        boolean bl = cacher != null;
        if (bl && cacher.checkAndCall(this)) {
            transformation = new Quaterniond();
            return;
        }

        for (double i = min; i < max; i+=inc) {
            func.accept(this, i);
        }
        if (bl) cacher.setHas(true);
        transformation = new Quaterniond();
    }

    @FunctionalInterface
    public interface ParticleSpawnMethod {
        void spawn(Level level, ParticleOptions options, double x, double y, double z, double xs, double ys, double zs);
    }

    public interface Cacher {
        void put(Vector3d pos, Vector3d mot);
        boolean has(ParticleShaper shaper);
        void setHas(boolean bl);
        void call(ParticleShaper shaper);
        default boolean checkAndCall(ParticleShaper shaper) {
            boolean bl = has(shaper);
            if (bl)
                call(shaper);
            return bl;
        }
    }

    public static class Combo extends ParticleShaper {
        public static Combo createCombo(ParticleOptions[] particle, BiConsumer<ParticleShaper, Double> func, double max, double min, double inc) {
            return new Combo(particle, func, max, min, inc);
        }

        protected final ParticleOptions[] particles;

        public Combo(ParticleOptions[] particles, BiConsumer<ParticleShaper, Double> func, double max, double min, double inc) {
            super(ParticleTypes.ASH, func, max, min, inc);
            this.particles = particles;
        }

        public Combo(Combo c) {
            this(c.particles, c.func, c.max, c.min, c.inc);
            this.cacher = c.cacher;
        }
        public Combo(ParticleOptions[] particles, ParticleShaper c) {
            this(particles, c.func, c.max, c.min, c.inc);
            this.cacher = c.cacher;
        }

        public ParticleOptions[] getParticles() {
            return particles;
        }

        @Override
        public void spawn(Vector3d vec, Vector3d mot) {
            if (cacher != null && !cacher.has(this)) cacher.put(vec, mot);
            if (transformation != null) {
                vec = vec.rotate(transformation);
                mot = mot.rotate(transformation);
            }
            for (ParticleOptions p : particles) {
                particleSpawner.spawn(level, p, vec.x+this.x, vec.y+this.y, vec.z+this.z, mot.x, mot.y, mot.z);
            }
        }
    }
}
