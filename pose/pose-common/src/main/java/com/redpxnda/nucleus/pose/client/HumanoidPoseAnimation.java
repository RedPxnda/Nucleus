package com.redpxnda.nucleus.pose.client;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
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
    public @AutoCodec.Optional boolean resetFirstPersonView = false;
    public @AutoCodec.Optional FrameMultiplier leftHandMultiplier = FrameMultiplier.LEFT_HAND_INVERT;

    @Override
    public void additionalSetup() {
        frames = frames.stream().sorted((f1, f2) -> Float.compare(f1.endTime, f2.endTime)).collect(Collectors.toCollection(ArrayList::new));
        length = frames.get(frames.size()-1).endTime;
    }

    @AutoCodec.Settings(optionalByDefault = true)
    @AutoCodec.Override("codec")
    public static class FrameMultiplier {
        public static final Codec<FrameMultiplier> codec = AutoCodec.of(FrameMultiplier.class).codec();
        public static final Vector3f ONE_VEC = new Vector3f(1, 1, 1);
        public static final FrameMultiplier EMPTY = new FrameMultiplier();
        public static final FrameMultiplier LEFT_HAND_INVERT = new FrameMultiplier(new Vector3f(-1, 1, 1), new Vector3f(1, -1, -1), ONE_VEC);

        public Vector3f position = ONE_VEC;
        public Vector3f rotation = ONE_VEC;
        public Vector3f scale = ONE_VEC;

        public FrameMultiplier() {
        }
        public FrameMultiplier(Vector3f position, Vector3f rotation, Vector3f scale) {
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
        }
    }

    @AutoCodec.Settings(optionalByDefault = true)
    @AutoCodec.Override("codec")
    public static class Frame {
        public static final Codec<Frame> codec = AutoCodec.of(Frame.class).codec();

        public PartState head = PartState.EMPTY;
        public PartState body = PartState.EMPTY;
        public PartState fpUsedArm = PartState.EMPTY; // fp = first person
        public PartState fpUnusedArm = PartState.EMPTY;
        public PartState fpRightArm = PartState.EMPTY;
        public PartState fpLeftArm = PartState.EMPTY;
        public PartState usedArm = PartState.EMPTY;
        public PartState unusedArm = PartState.EMPTY;
        public PartState rightArm = PartState.EMPTY;
        public PartState leftArm = PartState.EMPTY;
        public PartState usedItem = PartState.EMPTY;
        public PartState unusedItem = PartState.EMPTY;
        public PartState rightItem = PartState.EMPTY;
        public PartState leftItem = PartState.EMPTY;
        public PartState rightLeg = PartState.EMPTY;
        public PartState leftLeg = PartState.EMPTY;
        public InterpolateMode interpolate = InterpolateMode.NONE;
        public float endTime = 0; // in seconds

        public Frame() {
        }
        public Frame(PartState usedItem, PartState unusedItem, PartState rightItem, PartState leftItem) {
            this.usedItem = usedItem;
            this.unusedItem = unusedItem;
            this.rightItem = rightItem;
            this.leftItem = leftItem;
        }
        public Frame(PartState fpUsedArm, PartState fpUnusedArm, PartState fpRightArm, PartState fpLeftArm, InterpolateMode interpolate, float endTime) {
            this.fpUsedArm = fpUsedArm;
            this.fpUnusedArm = fpUnusedArm;
            this.fpRightArm = fpRightArm;
            this.fpLeftArm = fpLeftArm;
            this.interpolate = interpolate;
            this.endTime = endTime;
        }
        public Frame(PartState head, PartState body, PartState usedArm, PartState unusedArm, PartState rightArm, PartState leftArm, PartState rightLeg, PartState leftLeg, InterpolateMode interpolate, float endTime) {
            this.head = head;
            this.body = body;
            this.usedArm = usedArm;
            this.unusedArm = unusedArm;
            this.rightArm = rightArm;
            this.leftArm = leftArm;
            this.rightLeg = rightLeg;
            this.leftLeg = leftLeg;
            this.interpolate = interpolate;
            this.endTime = endTime;
        }
        public Frame(PartState head, PartState body, PartState fpUsedArm, PartState fpUnusedArm, PartState fpRightArm, PartState fpLeftArm, PartState usedArm, PartState unusedArm, PartState rightArm, PartState leftArm, PartState rightLeg, PartState leftLeg, InterpolateMode interpolate, float endTime) {
            this.head = head;
            this.body = body;
            this.fpUsedArm = fpUsedArm;
            this.fpUnusedArm = fpUnusedArm;
            this.fpRightArm = fpRightArm;
            this.fpLeftArm = fpLeftArm;
            this.usedArm = usedArm;
            this.unusedArm = unusedArm;
            this.rightArm = rightArm;
            this.leftArm = leftArm;
            this.rightLeg = rightLeg;
            this.leftLeg = leftLeg;
            this.interpolate = interpolate;
            this.endTime = endTime;
        }

        public Frame interpFpTo(float delta, Frame other) {
            return new Frame(
                    fpUsedArm.interpTo(interpolate, delta, other.fpUsedArm),
                    fpUnusedArm.interpTo(interpolate, delta, other.fpUnusedArm),
                    fpRightArm.interpTo(interpolate, delta, other.fpRightArm),
                    fpLeftArm.interpTo(interpolate, delta, other.fpLeftArm),
                    interpolate, endTime
            );
        }

        public Frame interpItemTo(float delta, Frame other) {
            return new Frame(
                    usedItem.interpTo(interpolate, delta, other.usedItem),
                    unusedItem.interpTo(interpolate, delta, other.unusedItem),
                    rightItem.interpTo(interpolate, delta, other.rightItem),
                    leftItem.interpTo(interpolate, delta, other.leftItem)
            );
        }

        public Frame interpTo(float delta, Frame other) {
            return new Frame(
                    head.interpTo(interpolate, delta, other.head),
                    body.interpTo(interpolate, delta, other.body),
                    usedArm.interpTo(interpolate, delta, other.usedArm),
                    unusedArm.interpTo(interpolate, delta, other.unusedArm),
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

        public PartState() {
        }
        public PartState(Vector3f position, Vector3f rotation, Vector3f scale) {
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
        }

        public PartState interpTo(InterpolateMode mode, float delta, PartState other) {
            return new PartState(
                    MathUtil.interpolateVector(mode, delta, position, other.position),
                    MathUtil.interpolateVector(mode, delta, rotation, other.rotation),
                    MathUtil.interpolateVector(mode, delta, scale, other.scale)
            );
        }

        @Override
        public void additionalSetup() {
            if (rotation != EMPTY_VEC) MathUtil.mapVector3f(rotation, MathUtil::radians);
        }

        public Matrix4f generateMatrix() {
            Matrix4f translationMatrix = new Matrix4f().translate(position);

            Matrix4f rotationMatrix = new Matrix4f()
                    .rotateX(rotation.x)
                    .rotateY(rotation.y)
                    .rotateZ(rotation.z);

            Matrix4f scaleMatrix = new Matrix4f().scale(scale);

            return new Matrix4f()
                    .mul(translationMatrix)
                    .mul(rotationMatrix)
                    .mul(scaleMatrix);
        }

        public Matrix4f generateMatrix(FrameMultiplier multiplier) {
            Matrix4f translationMatrix = new Matrix4f().translate(position.x*multiplier.position.x, position.y*multiplier.position.y, position.z*multiplier.position.z);

            Matrix4f rotationMatrix = new Matrix4f()
                    .rotateX(rotation.x*multiplier.rotation.x)
                    .rotateY(rotation.y*multiplier.rotation.y)
                    .rotateZ(rotation.z*multiplier.rotation.z);

            Matrix4f scaleMatrix = new Matrix4f().scale(scale.x*multiplier.scale.x, scale.y*multiplier.scale.y, scale.z*multiplier.scale.z);

            return new Matrix4f()
                    .mul(translationMatrix)
                    .mul(rotationMatrix)
                    .mul(scaleMatrix);
        }
    }
}
