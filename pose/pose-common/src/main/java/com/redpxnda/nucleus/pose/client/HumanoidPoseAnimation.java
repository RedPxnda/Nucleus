package com.redpxnda.nucleus.pose.client;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class HumanoidPoseAnimation implements AutoCodec.AdditionalConstructing {
    public static final Codec<HumanoidPoseAnimation> codec = AutoCodec.of(HumanoidPoseAnimation.class).codec();

    public List<Frame> frames;
    public @CodecBehavior.Optional int loops = 1; // -1 for indefinite
    public @AutoCodec.Ignored float length = -1;
    public @CodecBehavior.Optional Frame initialPose = new Frame();
    public @CodecBehavior.Optional boolean resetFirstPersonView = false;
    public @CodecBehavior.Optional FrameMultiplier leftHandMultiplier = FrameMultiplier.LEFT_HAND_INVERT;
    public @CodecBehavior.Optional String nextPose = null;

    @Override
    public void additionalSetup() {
        frames = frames.stream().sorted((f1, f2) -> Float.compare(f1.endTime, f2.endTime)).collect(Collectors.toCollection(ArrayList::new));
        length = frames.get(frames.size() - 1).endTime;
    }

    @AutoCodec.Settings(defaultOptionalBehavior = @CodecBehavior.Optional)
    @CodecBehavior.Override("codec")
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

    @AutoCodec.Settings(defaultOptionalBehavior = @CodecBehavior.Optional)
    @CodecBehavior.Override("codec")
    public static class Frame {
        public static final Codec<Frame> codec = AutoCodec.of(Frame.class).codec();

        public @Nullable PartState head;
        public @Nullable PartState body;
        public @Nullable PartState fpUsedArm; // fp = first person
        public @Nullable PartState fpUnusedArm;
        public @Nullable PartState fpRightArm;
        public @Nullable PartState fpLeftArm;
        public @Nullable PartState usedArm;
        public @Nullable PartState unusedArm;
        public @Nullable PartState rightArm;
        public @Nullable PartState leftArm;
        public @Nullable PartState usedItem;
        public @Nullable PartState unusedItem;
        public @Nullable PartState rightItem;
        public @Nullable PartState leftItem;
        public @Nullable PartState rightLeg;
        public @Nullable PartState leftLeg;
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

        public static @Nullable PartState interpolateOverFrames(
                List<Frame> frames,
                int currentIndex,
                int nextIndex,
                double elapsedTime,
                boolean looping,
                Function<Frame, @Nullable PartState> partExtractor
        ) {
            int size = frames.size();
            currentIndex = findFrameIndex(frames, elapsedTime, looping);
            nextIndex = (currentIndex + 1) % frames.size();


            double loopDuration = frames.get(size - 1).endTime * 20;

            if (looping) {
                elapsedTime = elapsedTime % loopDuration;
            }

            // Backward search for 'from' frame
            PartState from = null;
            Frame current = frames.get(currentIndex);
            int fromIndex = currentIndex;
            for (int i = 0; i < size; i++) {
                Frame f = frames.get(fromIndex);
                current = f;
                from = partExtractor.apply(f);
                if (from != null) break;

                int prev = (fromIndex - 1 + size) % size;
                if (!looping && prev > fromIndex) break; // avoid wrap in non-looping
                fromIndex = prev;

                if (fromIndex == nextIndex) break;
            }

            // Forward search for 'to' frame
            PartState to = null;
            int toIndex = nextIndex % size;
            for (int i = 0; i < size; i++) {
                Frame f = frames.get(toIndex);
                to = partExtractor.apply(f);
                if (to != null) break;

                int next = (toIndex + 1) % size;
                if (!looping && next < toIndex) break; // avoid wrap in non-looping
                toIndex = next;

                if (toIndex == fromIndex) break;
            }

            if (from == null && to == null) {
                return null;
            }

            if (from == null) from = PartState.EMPTY;
            if (to == null) to = PartState.EMPTY;


            double cTime = frames.get(fromIndex).endTime * 20;
            double nTime = frames.get(toIndex).endTime * 20;

            // Calculate wrapped distances
            double totalDist = timeDistance(cTime, nTime, looping, loopDuration);
            double elapsedDist = timeDistance(cTime, elapsedTime, looping, loopDuration);

            float actualDelta = (totalDist == 0) ? 0f : (float) (elapsedDist / totalDist);
            while (actualDelta > 1) {
                actualDelta--;
            }
            DecimalFormat df = new DecimalFormat("0.00");
            actualDelta = Math.max(0f, Math.min(1f, actualDelta));


            InterpolateMode mode = current.interpolate;
            return from.interpTo(mode, actualDelta, to);
        }

        public static int findFrameIndex(List<Frame> frames, double elapsedTime, boolean looping) {
            double loopDuration = frames.get(frames.size() - 1).endTime * 20;
            double time = looping ? elapsedTime % loopDuration : elapsedTime;

            for (int i = 0; i < frames.size() - 1; i++) {
                double t1 = frames.get(i).endTime * 20;
                double t2 = frames.get(i + 1).endTime * 20;
                if (time >= t1 && time < t2) return i;
            }

            return frames.size() - 1; // last frame
        }


        /**
         * Calculates the forward-wrapped distance from `fromTime` to `toTime`,
         * using the given `loopDuration` if looping is enabled.
         */
        private static double timeDistance(double fromTime, double toTime, boolean looping, double loopDuration) {
            double dist = toTime - fromTime;
            if (looping && dist < 0) {
                dist += loopDuration;
            }
            return dist;
        }

    }

    @AutoCodec.Settings(defaultOptionalBehavior = @CodecBehavior.Optional)
    @CodecBehavior.Override("codec")
    public static class PartState implements AutoCodec.AdditionalConstructing {
        public static final Codec<PartState> codec = AutoCodec.of(PartState.class).codec();
        public static final Vector3f EMPTY_VEC = new Vector3f();
        public static final Vector3f ONE_VEC = new Vector3f(1, 1, 1);
        public static final PartState EMPTY = new PartState();

        public Vector3f position = EMPTY_VEC;
        public Vector3f rotation = EMPTY_VEC;
        public Vector3f scale = ONE_VEC;
        public InterpolateMode interpolateMode = null;

        public PartState() {
        }

        public PartState(Vector3f position, Vector3f rotation, Vector3f scale, InterpolateMode mode) {
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
            this.interpolateMode = mode;
        }

        public PartState interpTo(InterpolateMode mode, float delta, PartState other) {
            return interpolatePartStateMatrix(interpolateMode == null ? mode : interpolateMode, delta, this, other);
        }

        public static PartState interpolatePartStateMatrix(InterpolateMode mode, float delta, PartState a, PartState b) {
            // Decompose a
            mode = a.interpolateMode == null ? mode : a.interpolateMode;
            Vector3f aTranslation = new Vector3f();
            Quaternionf aRotation = new Quaternionf();
            Vector3f aScale = new Vector3f();
            a.generateMatrix().getTranslation(aTranslation);
            a.generateMatrix().getUnnormalizedRotation(aRotation);
            a.generateMatrix().getScale(aScale);

            // Decompose b
            Vector3f bTranslation = new Vector3f();
            Quaternionf bRotation = new Quaternionf();
            Vector3f bScale = new Vector3f();
            b.generateMatrix().getTranslation(bTranslation);
            b.generateMatrix().getUnnormalizedRotation(bRotation);
            b.generateMatrix().getScale(bScale);

            // Interpolate translation and scale linearly
            Vector3f interpTranslation = new Vector3f();
            interpTranslation.x = (float) mode.interpolate(delta, aTranslation.x, bTranslation.x);
            interpTranslation.y = (float) mode.interpolate(delta, aTranslation.y, bTranslation.y);
            interpTranslation.z = (float) mode.interpolate(delta, aTranslation.z, bTranslation.z);

            Vector3f interpScale = new Vector3f();
            interpScale.x = (float) mode.interpolate(delta, aScale.x, bScale.x);
            interpScale.y = (float) mode.interpolate(delta, aScale.y, bScale.y);
            interpScale.z = (float) mode.interpolate(delta, aScale.z, bScale.z);

            // Interpolate rotation using SLERP
            Quaternionf interpRotation = new Quaternionf();
            aRotation.slerp(bRotation, delta, interpRotation);

            // Compose new matrix
            Matrix4f resultMatrix = new Matrix4f()
                    .translate(interpTranslation)
                    .rotate(interpRotation)
                    .scale(interpScale);

            // Decompose back to Euler angles for PartState
            Vector3f resultTranslation = new Vector3f();
            Quaternionf resultQuat = new Quaternionf();
            Vector3f resultScale = new Vector3f();

            resultMatrix.getTranslation(resultTranslation);
            resultMatrix.getUnnormalizedRotation(resultQuat);
            resultMatrix.getScale(resultScale);

            // Convert quaternion to Euler angles
            Vector3f resultEuler = resultQuat.getEulerAnglesXYZ(new Vector3f());

            return new PartState(resultTranslation, resultEuler, resultScale, a.interpolateMode);
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

        public Matrix4f generateMatrixScaled() {
            Matrix4f translationMatrix = new Matrix4f().translate(position.mul(1 / 16f));

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
            Matrix4f translationMatrix = new Matrix4f().translate(position.x * multiplier.position.x, position.y * multiplier.position.y, position.z * multiplier.position.z);

            Matrix4f rotationMatrix = new Matrix4f()
                    .rotateX(rotation.x * multiplier.rotation.x)
                    .rotateY(rotation.y * multiplier.rotation.y)
                    .rotateZ(rotation.z * multiplier.rotation.z);

            Matrix4f scaleMatrix = new Matrix4f().scale(scale.x * multiplier.scale.x, scale.y * multiplier.scale.y, scale.z * multiplier.scale.z);

            return new Matrix4f()
                    .mul(translationMatrix)
                    .mul(rotationMatrix)
                    .mul(scaleMatrix);
        }
    }
}
