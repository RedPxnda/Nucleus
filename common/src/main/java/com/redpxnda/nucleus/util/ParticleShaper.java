package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.math.AxisD;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.network.clientbound.ParticleCreationPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.function.BiConsumer;

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
    public static ParticleShaper expandingCircle(ParticleOptions options, double radius, double inc, double speed) {
        return new ParticleShaper(
                options,
                (s, i) -> {
                    double rads = i * Math.PI/180;
                    s.spawn(Math.sin(rads)*radius, Math.cos(rads)*radius, Math.sin(rads)*speed, Math.cos(rads)*speed);
                }, 360, inc
        );
    }
    public static ParticleShaper circle(ParticleOptions options, double radius) {
        return circle(options, radius, 1);
    }
    public static ParticleShaper square(ParticleOptions options, double r, int max, int inc) {
        double[][] setup = new double[][]{
                {0, 0},
                {r, 0},
                {r, r},
                {0, r},
        };
        return polygon(setup, options, r, max, inc);
    }
    public static ParticleShaper eqTriangle(ParticleOptions options, double r, int max, int inc) {
        double[][] setup = new double[][]{
                {0, 0},
                {r/2, Math.tan(60 * Math.PI/180f)*r/2},
                {r, 0}
        };
        return polygon(setup, options, r, max, inc);
    }
    public static ParticleShaper rightTriangle(ParticleOptions options, double r, int max, int inc) {
        double[][] setup = new double[][]{
                {0, 0},
                {r, 0},
                {r, r}
        };
        return polygon(setup, options, r, max, inc);
    }
    public static ParticleShaper polygon(double[][] shape, ParticleOptions options, double r, int max, int inc) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.arrayLerp2(i.intValue(), max, shape);
            s.spawn(doubles[0]-r/2, doubles[1]-r/2);
        }, max, inc);
    }
    public static ParticleShaper bezier(double[][] controls, ParticleOptions options, double inc) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.bezier(controls, i);
            s.spawn(doubles[0], doubles[1]);
        }, 1, inc);
    }
    public static ParticleShaper expandingPolygon(double[][] shape, ParticleOptions options, double r, int max, int inc, double speed) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.arrayLerp2(i.intValue(), max, shape);
            s.spawn(doubles[0]-r/2, doubles[1]-r/2, (doubles[0]-r/2)/r * speed, (doubles[1]-r/2)/r * speed);
        }, max, inc);
    }
    public static ParticleShaper polygon(double[][] shape, ParticleOptions options, int max, int inc) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.arrayLerp2(i.intValue(), max, shape);
            s.spawn(doubles[0], doubles[1]);
        }, max, inc);
    }
    public static ParticleShaper expandingPolygon(double[][] shape, ParticleOptions options, int max, int inc, double speed) {
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MathUtil.arrayLerp2(i.intValue(), max, shape);
            s.spawn(doubles[0], doubles[1], doubles[0] * speed, doubles[1] * speed);
        }, max, inc);
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
    public static ParticleShaper square(ParticleOptions options, double r) {
        return square(options, r, 100, 1);
    }
    public static ParticleShaper create(ParticleOptions particle, BiConsumer<ParticleShaper, Double> func, double max, double min, double inc) {
        return new ParticleShaper(particle, func, max, min, inc);
    }

    private final double max;
    private final double min;
    private final double inc;
    private double x;
    private double y;
    private double z;
    private Quaterniond transformation;
    private Level level;
    private ParticleSpawnMethod particleSpawner;
    private final BiConsumer<ParticleShaper, Double> func;
    private final ParticleOptions particle;

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
    }
    public static ParticleShaper duplicate(ParticleShaper o) {
        ParticleShaper s = new ParticleShaper(o);
        s.x = o.x;
        s.y = o.y;
        s.z = o.z;
        s.transformation = o.transformation;
        s.level = o.level;
        s.particleSpawner = o.particleSpawner;
        return s;
    }

    public ParticleOptions particle() {
        return particle;
    }

    public void spawn(double x, double z) {
        Vector3d vec = new Vector3d(x, 0, z);
        if (transformation != null)
            vec = vec.rotate(transformation);
        particleSpawner.spawn(level, particle(), vec.x+this.x, vec.y+this.y, vec.z+this.z, 0, 0, 0);
    }
    public void spawn(double x, double z, double xs, double zs) {
        Vector3d vec = new Vector3d(x, 0, z);
        Vector3d mot = new Vector3d(xs, 0, zs);
        if (transformation != null) {
            vec = vec.rotate(transformation);
            mot = mot.rotate(transformation);
        }
        particleSpawner.spawn(level, particle(), vec.x+this.x, vec.y+this.y, vec.z+this.z, mot.x, mot.y, mot.z);
    }
    public void spawn(double x, double y, double z) {
        Vector3d vec = new Vector3d(x, y, z);
        if (transformation != null)
            vec = vec.rotate(transformation);
        particleSpawner.spawn(level, particle(), vec.x+this.x, vec.y+this.y, vec.z+this.z, 0, 0, 0);
    }
    public void spawn(double x, double y, double z, double xs, double ys, double zs) {
        Vector3d vec = new Vector3d(x, y, z);
        Vector3d mot = new Vector3d(xs, ys, zs);
        if (transformation != null) {
            vec = vec.rotate(transformation);
            mot = mot.rotate(transformation);
        }
        particleSpawner.spawn(level, particle(), vec.x+this.x, vec.y+this.y, vec.z+this.z, mot.x, mot.y, mot.z);
    }
    public void spawn(ParticleOptions options, double x, double y, double z, double xs, double ys, double zs) {
        Vector3d vec = new Vector3d(x, y, z);
        Vector3d mot = new Vector3d(xs, ys, zs);
        if (transformation != null) {
            vec = vec.rotate(transformation);
            mot = mot.rotate(transformation);
        }
        particleSpawner.spawn(level, options, vec.x+this.x, vec.y+this.y, vec.z+this.z, mot.x, mot.y, mot.z);
    }

    public ParticleShaper transform(Quaterniond transformation) {
        if (this.transformation != null)
            this.transformation.mul(transformation);
        else
            this.transformation = transformation;
        return this;
    }
    public Quaterniond getTransformation() {
        return transformation;
    }

    public ParticleShaper fromClient() {
        this.particleSpawner = Level::addParticle;
        return this;
    }
    public ParticleShaper fromServer() {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> ParticleCreationPacket.send(((ServerLevel) l), op, x, y, z, xs, ys, zs);
        return this;
    }
    public ParticleShaper fromServer(int count, double maxSpeed) {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> ((ServerLevel) l).sendParticles(op, x, y, z, count, xs, ys, zs, maxSpeed);
        return this;
    }
    public ParticleShaper fromServer(ServerPlayer player) {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> Nucleus.CHANNEL.sendToPlayer(player, new ParticleCreationPacket(op, x, y, z, xs, ys, zs));
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

        for (double i = min; i < max; i+=inc) {
            func.accept(this, i);
        }
    }
    public void loopBetween(Level level, double x, double z, double minY, double maxY, double yInc) {
        this.level = level;
        this.x = x;
        this.z = z;

        for (double j = minY; j < maxY; j+=yInc) {
            y = j;
            for (double i = min; i < max; i+=inc) {
                func.accept(this, i);
            }
        }
    }
    public void runBetween(Level level, double x, double z, double startY, double yInc) {
        this.level = level;
        this.x = x;
        this.y = startY;
        this.z = z;

        for (double i = min; i < max; i+=inc) {
            if (i > 0) y+=yInc;
            func.accept(this, i);
        }
    }

    @FunctionalInterface
    public interface ParticleSpawnMethod {
        void spawn(Level level, ParticleOptions options, double x, double y, double z, double xs, double ys, double zs);
    }
}
