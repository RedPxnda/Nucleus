package com.redpxnda.nucleus.math;

import org.joml.Quaterniond;
import org.joml.Vector3d;

@FunctionalInterface
public interface AxisD {
    AxisD XN = f -> new Quaterniond().rotationX(-f);
    AxisD XP = f -> new Quaterniond().rotationX(f);
    AxisD YN = f -> new Quaterniond().rotationY(-f);
    AxisD YP = f -> new Quaterniond().rotationY(f);
    AxisD ZN = f -> new Quaterniond().rotationZ(-f);
    AxisD ZP = f -> new Quaterniond().rotationZ(f);

    public static AxisD of(Vector3d vec) {
        return f -> new Quaterniond().rotateAxis(f, vec);
    }

    Quaterniond rotation(float var1);

    default Quaterniond rotationDegrees(float f) {
        return this.rotation(f * ((float)Math.PI / 180));
    }
}
