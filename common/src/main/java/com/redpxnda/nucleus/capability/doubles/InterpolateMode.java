package com.redpxnda.nucleus.capability.doubles;

import com.google.gson.JsonElement;
import com.redpxnda.nucleus.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface InterpolateMode {
    InterpolateMode NONE = (delta, last, current) -> current;
    InterpolateMode LERP = MathUtil::lerp;
    InterpolateMode COS = (delta, last, current) -> {
        double d = (1 - Math.cos(delta * Math.PI)) / 2;
        return MathUtil.lerp(d, last, current);
    };

    double interpolate(float delta, double last, double current);

    @Environment(EnvType.CLIENT)
    interface Creator {
        InterpolateMode createFrom(JsonElement element);
    }

    @Environment(EnvType.CLIENT)
    record EaseIn(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(Math.pow(delta, amplifier), last, current);
        }
    }

    @Environment(EnvType.CLIENT)
    record EaseOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier)), last, current);
        }
    }

    @Environment(EnvType.CLIENT)
    record EaseInOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            float flippedDelta = MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier));
            float normalDelta = MathUtil.pow(delta, amplifier);
            float finalDelta = MathUtil.lerp(delta, normalDelta, flippedDelta);
            return MathUtil.lerp(finalDelta, last, current);
        }
    }
}
