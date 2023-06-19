package com.redpxnda.nucleus.math;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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

    static AxisD of(Vector3d vec) {
        return f -> new Quaterniond().rotateAxis(f, vec);
    }

    Quaterniond rotation(double var1);

    default Quaterniond rotationDegrees(double f) {
        return this.rotation(f * (Math.PI / 180));
    }

    final class Codecs {
        public static final Codec<Quaterniond> direction = Codec.pair(
                Codec.STRING.fieldOf("direction").codec(),
                Codec.DOUBLE.fieldOf("amount").codec()
        ).flatComapMap(p -> {
            switch (p.getFirst().toLowerCase()) {
                case "xn" -> { return XN.rotationDegrees(p.getSecond()); }
                case "xp" -> { return XP.rotationDegrees(p.getSecond()); }
                case "yn" -> { return YN.rotationDegrees(p.getSecond()); }
                case "zn" -> { return ZN.rotationDegrees(p.getSecond()); }
                case "zp" -> { return ZP.rotationDegrees(p.getSecond()); }
                default   -> { return YP.rotationDegrees(p.getSecond()); }
            }
        }, q -> DataResult.error("Cannot turn quaternion into direction and amount."));
        public static final  Codec<Quaterniond> directions = direction.listOf().flatComapMap(l -> {
            Quaterniond quaternion = null;
            for (Quaterniond q : l) {
                if (quaternion == null) quaternion = q;
                else quaternion.mul(q);
            }
            return quaternion == null ? new Quaterniond() : quaternion;
        }, q -> DataResult.error("Cannot turn Quaternion into list of quaternions."));
        public static final Codec<Quaterniond> all = Codec.either(directions, direction).xmap(e -> e.left().isPresent() ? e.left().get() : e.right().get(), Either::right);
    }
}
