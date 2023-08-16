package com.redpxnda.nucleus.pose;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class HumanoidPoseAnimation implements AutoCodec.AdditionalConstructing {
    public static final Codec<HumanoidPoseAnimation> codec = AutoCodec.of(HumanoidPoseAnimation.class).codec();

    public List<Frame> frames;
    public @AutoCodec.Optional int loops = 1; // -1 for indefinite
    public @AutoCodec.Ignored float length = -1;
    public @AutoCodec.Optional Frame initialPose = null;

    @Override
    public void additionalSetup() {
        frames = frames.stream().sorted((f1, f2) -> Float.compare(f1.endTime, f2.endTime)).collect(Collectors.toCollection(ArrayList::new));
        length = frames.get(frames.size()-1).endTime;
    }

    @AutoCodec.Settings(optionalByDefault = true)
    @AutoCodec.Override("codec")
    public static class Frame {
        public static final Codec<Frame> codec = AutoCodec.of(Frame.class).codec();

        public PartState head = PartState.EMPTY;
        public PartState body = PartState.EMPTY;
        public PartState rightArm = PartState.EMPTY;
        public PartState leftArm = PartState.EMPTY;
        public PartState rightLeg = PartState.EMPTY;
        public PartState leftLeg = PartState.EMPTY;
        public InterpolateMode interpolate = InterpolateMode.NONE;
        public float endTime = 0; // in seconds

        public Frame() {
        }
        public Frame(PartState head, PartState body, PartState rightArm, PartState leftArm, PartState rightLeg, PartState leftLeg, InterpolateMode interpolate, float endTime) {
            this.head = head;
            this.body = body;
            this.rightArm = rightArm;
            this.leftArm = leftArm;
            this.rightLeg = rightLeg;
            this.leftLeg = leftLeg;
            this.interpolate = interpolate;
            this.endTime = endTime;
        }

        public Frame interpTo(float delta, Frame other) {
            return new Frame(
                    head.interpTo(interpolate, delta, other.head),
                    body.interpTo(interpolate, delta, other.body),
                    rightArm.interpTo(interpolate, delta, other.rightArm),
                    leftArm.interpTo(interpolate, delta, other.leftArm),
                    rightLeg.interpTo(interpolate, delta, other.rightLeg),
                    leftLeg.interpTo(interpolate, delta, other.leftLeg),
                    interpolate, endTime
            );
        }
    }

    @AutoCodec.Settings(optionalByDefault = true)
    @AutoCodec.Override("codec")
    public static class PartState implements AutoCodec.AdditionalConstructing {
        public static final Codec<PartState> codec = AutoCodec.of(PartState.class).codec();
        public static final Vector3f EMPTY_VEC = new Vector3f();
        public static final Vector3f ONE_VEC = new Vector3f(1, 1, 1);
        public static final PartState EMPTY = new PartState();

        public Vector3f position = EMPTY_VEC;
        public Vector3f rotation = EMPTY_VEC;
        public Vector3f scale = ONE_VEC;
        public boolean visible = true;

        public PartState() {
        }
        public PartState(Vector3f position, Vector3f rotation, Vector3f scale, boolean visible) {
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
            this.visible = visible;
        }

        public PartState interpTo(InterpolateMode mode, float delta, PartState other) {
            if (delta < .5 && !visible) return this;
            if (delta >= .5 && !other.visible) return other;

            return new PartState(
                    MathUtil.interpolateVector(mode, delta, position, other.position),
                    MathUtil.interpolateVector(mode, delta, rotation, other.rotation),
                    MathUtil.interpolateVector(mode, delta, scale, other.scale),
                    true
            );
        }

        @Override
        public void additionalSetup() {
            if (rotation != EMPTY_VEC) MathUtil.mapVector3f(rotation, MathUtil::radians);
        }
    }
}
