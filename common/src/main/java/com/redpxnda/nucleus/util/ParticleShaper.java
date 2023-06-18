package com.redpxnda.nucleus.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class ParticleShaper {
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
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MiscUtil.arrayLerp2(i.intValue(), max, setup);
            s.spawn(doubles[0]-r/2, doubles[1]-r/2);
        }, max, inc);
    }
    public static ParticleShaper expandingSquare(ParticleOptions options, double r, int max, int inc, double speed) {
        double[][] setup = new double[][]{
                {0, 0},
                {r, 0},
                {r, r},
                {0, r},
        };
        return new ParticleShaper(options, (s, i) -> {
            double[] doubles = MiscUtil.arrayLerp2(i.intValue(), max, setup);
            s.spawn(doubles[0]-r/2, doubles[1]-r/2, doubles[0]/r * speed, doubles[1]/r * speed);
        }, max, inc);
    }
    public static ParticleShaper square(ParticleOptions options, double r) {
        return square(options, r, 100, 1);
    }
    private final double max;
    private final double inc;
    private double x;
    private double y;
    private double z;
    private Level level;
    private ParticleSpawnMethod particleSpawner;
    private final BiConsumer<ParticleShaper, Double> func;
    private final ParticleOptions particle;

    public ParticleShaper(ParticleOptions particle, BiConsumer<ParticleShaper, Double> func, double max, double inc) {
        this.max = max;
        this.inc = inc;
        this.func = func;
        this.particle = particle;
    }

    public ParticleOptions particle() {
        return particle;
    }

    public void spawn(double x, double z) {
        particleSpawner.spawn(level, particle(), this.x+x, y, this.z+z, 0, 0, 0);
    }
    public void spawn(double x, double z, double xs, double zs) {
        particleSpawner.spawn(level, particle(), this.x+x, y, this.z+z, xs, 0, zs);
    }
    public void spawn(double x, double y, double z) {
        particleSpawner.spawn(level, particle(), this.x+x, this.y+y, this.z+z, 0, 0, 0);
    }
    public void spawn(double x, double y, double z, double xs, double ys, double zs) {
        particleSpawner.spawn(level, particle(), this.x+x, this.y+y, this.z+z, xs, ys, zs);
    }
    public void spawn(ParticleOptions options, double x, double y, double z, double xs, double ys, double zs) {
        particleSpawner.spawn(level, options, this.x+x, this.y+y, this.z+z, xs, ys, zs);
    }

    public ParticleShaper fromClient() {
        this.particleSpawner = Level::addParticle;
        return this;
    }
    public ParticleShaper fromServer() {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> ((ServerLevel) l).sendParticles(op, x, y, z, 1, xs, ys, zs, 100);
        return this;
    }
    public ParticleShaper fromServer(int count, double maxSpeed) {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> ((ServerLevel) l).sendParticles(op, x, y, z, count, xs, ys, zs, maxSpeed);
        return this;
    }
    public ParticleShaper fromServer(ServerPlayer player) {
        this.particleSpawner = (l, op, x, y, z, xs, ys, zs) -> ((ServerLevel) l).sendParticles(player, op, false, x, y, z, 1, xs, ys, zs, 100);
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

        for (double i = 0; i < max; i+=inc) {
            func.accept(this, i);
        }
    }
    public void loopBetween(Level level, double x, double z, double minY, double maxY, double yInc) {
        this.level = level;
        this.x = x;
        this.z = z;

        for (double j = minY; j < maxY; j+=yInc) {
            y = j;
            for (double i = 0; i < max; i+=inc) {
                func.accept(this, i);
            }
        }
    }
    public void runBetween(Level level, double x, double z, double startY, double yInc) {
        this.level = level;
        this.x = x;
        this.y = startY;
        this.z = z;

        for (double i = 0; i < max; i+=inc) {
            if (i > 0) y+=yInc;
            func.accept(this, i);
        }
    }

    @FunctionalInterface
    public interface ParticleSpawnMethod {
        void spawn(Level level, ParticleOptions options, double x, double y, double z, double xs, double ys, double zs);
    }
}
