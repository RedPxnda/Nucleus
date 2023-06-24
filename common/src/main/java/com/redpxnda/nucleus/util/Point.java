package com.redpxnda.nucleus.util;

import com.redpxnda.nucleus.math.ParticleShaper;
import org.joml.Vector3d;

public class Point {
    private final double x;
    private final double y;
    private final double z;
    private final double xs;
    private final double ys;
    private final double zs;

    public Point(double x, double y, double z, double xs, double ys, double zs) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xs = xs;
        this.ys = ys;
        this.zs = zs;
    }

    public Point(Vector3d pos, Vector3d motion) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.xs = motion.x;
        this.ys = motion.y;
        this.zs = motion.z;
    }

    public void spawn(ParticleShaper s) {
        s.spawn(x, y, z, xs, ys, zs);
    }
}
