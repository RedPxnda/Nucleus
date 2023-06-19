package com.redpxnda.nucleus.math;

import net.minecraft.util.Mth;

import java.util.Arrays;

public class MathUtil {
    public static double[] arrayLerp2(int time, int maxTime, double[][] items) {
        if (items.length < 1) return new double[] { 1, 1 };
        int prog = (time % maxTime*2);
        if (prog >= maxTime) prog = prog-maxTime;

        int index = (int) Math.floor((prog/(float)maxTime)*items.length);
        float progress = ((prog/(float)maxTime)*items.length)-index;

        boolean tooLarge = index+1 >= items.length;
        return new double[] {
                Mth.lerp(progress, items[index][0], items[tooLarge ? 0 : index+1][0]),
                Mth.lerp(progress, items[index][1], items[tooLarge ? 0 : index+1][1])
        };
    }

    public static double[] arrayLerp3(int time, int maxTime, double[][] items) {
        if (items.length < 1) return new double[] { 1, 1, 1 };
        int prog = (time % maxTime*2);
        if (prog >= maxTime) prog = prog-maxTime;

        int index = (int) Math.floor((prog/(float)maxTime)*items.length);
        float progress = ((prog/(float)maxTime)*items.length)-index;

        boolean tooLarge = index+1 >= items.length;
        return new double[] {
                Mth.lerp(progress, items[index][0], items[tooLarge ? 0 : index+1][0]),
                Mth.lerp(progress, items[index][1], items[tooLarge ? 0 : index+1][1]),
                Mth.lerp(progress, items[index][2], items[tooLarge ? 0 : index+1][2])
        };
    }

    public static double[] bezier(double[][] points, double t) {
        if (points.length == 1)
            return points[0];
        else {
            double[] p1 = bezier(Arrays.copyOfRange(points, 0, points.length-1), t);
            double[] p2 = bezier(Arrays.copyOfRange(points, 1, points.length), t);
            return new double[]{
                    (1-t)*p1[0] + t*p2[0],
                    (1-t)*p1[1] + t*p2[1]
            };
        }
    }
}
