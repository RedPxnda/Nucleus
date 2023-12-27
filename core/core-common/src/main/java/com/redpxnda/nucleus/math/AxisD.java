package com.redpxnda.nucleus.math;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.util.MiscUtil;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.ArrayList;

@FunctionalInterface
public interface AxisD {
    Codec<Quaterniond> codec = Codec.DOUBLE.listOf().xmap(
            l -> new Quaterniond().rotationXYZ(Math.toRadians(l.get(0)), Math.toRadians(l.get(1)), Math.toRadians(l.get(2))),
            q -> MiscUtil.initialize(new ArrayList<>(), list -> {
                Vector3d v = q.getEulerAnglesXYZ(new Vector3d());
                list.add(v.x);
                list.add(v.y);
                list.add(v.z);
            })
    );

    AxisD XN = f -> new Quaterniond().rotationX(-f);
    AxisD XP = f -> new Quaterniond().rotationX(f);
    AxisD YN = f -> new Quaterniond().rotationY(-f);
    AxisD YP = f -> new Quaterniond().rotationY(f);
    AxisD ZN = f -> new Quaterniond().rotationZ(-f);
    AxisD ZP = f -> new Quaterniond().rotationZ(f);

    static AxisD of(Vector3d vec) {
        return f -> new Quaterniond().rotateAxis(f, vec);
    }

    Quaterniond rotation(double var1);

    default Quaterniond rotationDegrees(double f) {
        return this.rotation(f * (Math.PI / 180));
    }
}
