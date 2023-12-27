package com.redpxnda.nucleus.math;

import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class MathUtil extends MathHelper {
    public static final Random random = new Random();

    public static double random(double min, double max) {
        if (min == max) return min;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    public static float random(float min, float max) {
        if (min == max) return min;
        return ThreadLocalRandom.current().nextFloat(min, max);
    }
    public static int random(int min, int max) {
        if (min == max) return min;
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double[] arrayLerp2(int time, int maxTime, double[][] items) {
        if (items.length < 1) return new double[] { 1, 1 };
        int prog = (time % maxTime*2);
        if (prog >= maxTime) prog = prog-maxTime;

        int index = (int) Math.floor((prog/(float)maxTime)*items.length);
        float progress = ((prog/(float)maxTime)*items.length)-index;

        boolean tooLarge = index+1 >= items.length;
        return new double[] {
                lerp(progress, items[index][0], items[tooLarge ? 0 : index+1][0]),
                lerp(progress, items[index][1], items[tooLarge ? 0 : index+1][1])
        };
    }

    public static float flip(float val) {
        return 1 - val;
    }
    public static float pow(float base, float exponent) {
        return (float) Math.pow(base, exponent);
    }
    public static float radians(float degrees) {
        return (float) Math.toRadians(degrees);
    }

    public static void mapVector3f(Vector3f vec, Function<Float, Float> mapper) {
        vec.x = mapper.apply(vec.x);
        vec.y = mapper.apply(vec.y);
        vec.z = mapper.apply(vec.z);
    }
    public static Vector3f interpolateVector(InterpolateMode mode, float delta, Vector3f start, Vector3f end) {
        Vector3f vec = new Vector3f();
        vec.x = (float) mode.interpolate(delta, start.x, end.x);
        vec.y = (float) mode.interpolate(delta, start.y, end.y);
        vec.z = (float) mode.interpolate(delta, start.z, end.z);
        return vec;
    }

    public static double[] arrayLerp3(int time, int maxTime, double[][] items) {
        if (items.length < 1) return new double[] { 1, 1, 1 };
        int prog = (time % maxTime*2);
        if (prog >= maxTime) prog = prog-maxTime;

        int index = (int) Math.floor((prog/(float)maxTime)*items.length);
        float progress = ((prog/(float)maxTime)*items.length)-index;

        boolean tooLarge = index+1 >= items.length;
        return new double[] {
                lerp(progress, items[index][0], items[tooLarge ? 0 : index+1][0]),
                lerp(progress, items[index][1], items[tooLarge ? 0 : index+1][1]),
                lerp(progress, items[index][2], items[tooLarge ? 0 : index+1][2])
        };
    }

    public static double[] bezier(double[][] points, double t) {
        if (points.length == 1)
            return points[0];
        else {
            double[] p1 = bezier(Arrays.copyOfRange(points, 0, points.length-1), t);
            double[] p2 = bezier(Arrays.copyOfRange(points, 1, points.length), t);
            boolean is2D = p1.length < 3 || p2.length < 3;
            int zi = is2D ? 1 : 2;
            return new double[]{
                    (1-t)*p1[0] + t*p2[0],
                    is2D ? 0 : (1-t)*p1[1] + t*p2[1],
                    (1-t)*p1[zi] + t*p2[zi]
            };
        }
    }

    public static double solveBrent(Function<Double, Double> f, double a, double b, double eps, int maxSteps) {
        double tmp, s;

        // Swap a and b if b is lower than a
        if (b < a) {
            tmp = a;
            a = b;
            b = tmp;
        }

        double fa = f.apply(a);
        double fb = f.apply(b);

        // Check if a or b is already a root
        if (Math.abs(fa) <= eps) return a;
        if (Math.abs(fb) <= eps) return b;
        if (b == a) return Double.NaN;

        // If root is not bracketed, perform random search
        if (fa * fb > 0) {
            boolean rndflag = false;
            double ap, bp;
            for (int i = 0; i < maxSteps; i++) {
                ap = Math.random() * (b - a) + a;
                bp = Math.random() * (b - a) + a;
                if (bp < ap) {
                    tmp = ap;
                    ap = bp;
                    bp = tmp;
                }
                fa = f.apply(ap);
                fb = f.apply(bp);
                if (Math.abs(fa) <= eps) return ap;
                if (Math.abs(fb) <= eps) return bp;
                if (fa * fb < 0) {
                    rndflag = true;
                    a = ap;
                    b = bp;
                    break;
                }
            }
            if (!rndflag) return Double.NaN;
        }

        double c = a;
        double d = c;
        double fc = f.apply(c);

        if (Math.abs(fa) < Math.abs(fb)) {
            tmp = a;
            a = b;
            b = tmp;
            tmp = fa;
            fa = fb;
            fb = tmp;
        }

        int iter = 0;

        // Perform Brent's algorithm
        while ((Math.abs(fb) > eps) && (Math.abs(b - a) > eps) && (iter < maxSteps)) {
            if ((fa != fc) && (fb != fc)) {
                double c0 = (a * fb * fc) / ((fa - fb) * (fa - fc));
                double c1 = (b * fa * fc) / ((fb - fa) * (fb - fc));
                double c2 = (c * fa * fb) / ((fc - fa) * (fc - fb));
                s = c0 + c1 + c2;
            } else {
                s = b - (fb * (b - a)) / (fb - fa);
            }

            if ((s < (3 * (a + b) / 4) || s > b) ||
                    (Math.abs(s - b) >= (Math.abs(b - c) / 2)) ||
                    (Math.abs(s - b) >= (Math.abs(c - d) / 2))) {
                s = (a + b) / 2;
            }

            double fs = f.apply(s);
            d = c;
            c = b;
            fc = fb;

            if ((fa * fs) < 0) {
                b = s;
            } else {
                a = s;
            }

            if (Math.abs(fa) < Math.abs(fb)) {
                tmp = a;
                a = b;
                b = tmp;
                tmp = fa;
                fa = fb;
                fb = tmp;
            }

            iter++;
        }

        return roundWithEps(b, eps*10);
    }
    public static double roundWithEps(double value, double eps) {
        double scale = (1/eps);
        return Math.round(value * scale) / scale;
    }
}
