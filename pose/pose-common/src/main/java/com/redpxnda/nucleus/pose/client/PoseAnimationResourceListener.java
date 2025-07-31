package com.redpxnda.nucleus.pose.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.codec.misc.MiscCodecs;
import com.redpxnda.nucleus.event.RenderEvents;
import com.redpxnda.nucleus.registry.NucleusNamespaces;
import com.redpxnda.nucleus.util.MiscUtil;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.redpxnda.nucleus.pose.client.HumanoidPoseAnimation.Frame.interpolateOverFrames;

@Environment(EnvType.CLIENT)
public class PoseAnimationResourceListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = Nucleus.getLogger();
    public static final Map<String, HumanoidPoseAnimation> animations = new HashMap<>();

    public PoseAnimationResourceListener() {
        super(Nucleus.GSON, "poses");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        animations.clear();
        files.forEach((key, value) -> {
            if (!NucleusNamespaces.isNamespaceValid(key.getNamespace())) return;
            List<JsonObject> list = new ArrayList<>();
            if (value instanceof JsonObject object)
                list.add(object);
            else if (value instanceof JsonArray array)
                array.forEach(e -> {
                    if (e instanceof JsonObject object)
                        list.add(object);
                });

            list.forEach(obj -> animations.put(
                    ResourceLocation.parse(obj.get("name").getAsString()).toString(),
                    MiscCodecs.quickParse(obj, HumanoidPoseAnimation.codec, s -> MiscUtil.logError(LOGGER, "Failed to parse HumanoidPoseAnimation at " + key + "! -> " + s))
            ));
        });
    }

    @Environment(EnvType.CLIENT)
    public static void init() {
        /*RenderEvents.CHANGE_RENDERED_HANDS.register((player, hands) -> {
            hands.setOffhand(true);
            hands.setMainhand(true);
        });*/
        RenderEvents.ITEM_HAND_LAYER_RENDER.register((model, player, stack, displayContext, arm, matrices, buffer, light) -> {
            ClientPoseFacet cap = ClientPoseFacet.get(player);
            if (cap == null || cap.animation == null) return EventResult.pass();
            cap.frameIndex = cap.frameIndex % cap.animation.frames.size();

            HumanoidArm playerArm = player.getMainArm();
            boolean isUsedArm = (arm == playerArm && cap.usedHand == InteractionHand.MAIN_HAND) || (arm != playerArm && cap.usedHand != InteractionHand.MAIN_HAND);
            boolean isRightArm = arm == HumanoidArm.RIGHT;
            Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> relPartState = isUsedArm ? frame -> frame.usedItem : frame -> frame.unusedItem;
            Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> exactPartState = isRightArm ? frame -> frame.rightItem : frame -> frame.leftItem;
            HumanoidPoseAnimation animation = cap.animation;

            HumanoidPoseAnimation.FrameMultiplier leftHandMult = isRightArm ? null : animation.leftHandMultiplier;

            if (animation.initialPose != null) {
                positionMatricesToState(leftHandMult, relPartState.apply(animation.initialPose), exactPartState.apply(animation.initialPose), matrices);
            }

            if (animation.frames.size() == 1) {
                HumanoidPoseAnimation.Frame fm = animation.frames.get(0);
                positionMatricesToState(leftHandMult, relPartState.apply(fm), exactPartState.apply(fm), matrices);
            } else if (animation.frames.size() > 0) {
                float maxLength = animation.length * 20f;
                double elapsedTime = Rendering.getGameAndPartialTime() - cap.updateTime;
                if ((animation.loops == -1 || (animation.loops > 1 && elapsedTime < maxLength * animation.loops)) && elapsedTime >= maxLength) {
                    cap.frameIndex = 0;
                    elapsedTime %= maxLength;
                } else elapsedTime = Math.min(elapsedTime, maxLength);

                HumanoidPoseAnimation.Frame frame = animation.frames.get(cap.frameIndex % cap.animation.frames.size());
                while (frame.endTime * 20 < elapsedTime) {
                    cap.frameIndex++;
                    frame = animation.frames.get(cap.frameIndex);
                }
                int nextIndex = cap.frameIndex + 1;

// Interpolate over full frame list for both parts
                HumanoidPoseAnimation.PartState relState = interpolateOverFrames(animation.frames, cap.frameIndex, nextIndex, elapsedTime, animation.loops != 0, relPartState);
                HumanoidPoseAnimation.PartState exactState = interpolateOverFrames(animation.frames, cap.frameIndex, nextIndex, elapsedTime, animation.loops != 0, exactPartState);

// Apply them to matrices
                positionMatricesToState(leftHandMult, relState, exactState, matrices);
            }
            return EventResult.pass();
        });
        RenderEvents.RENDER_ARM_WITH_ITEM.register((stage, armRenderer, player, matrices, buffer, stack, hand, partialTicks, pitch, swingProgress, equippedProgress, combinedLight) -> {
            if (stage == RenderEvents.ArmRenderStage.ARM || stage == RenderEvents.ArmRenderStage.ITEM) {
                ClientPoseFacet cap = ClientPoseFacet.get(player);
                if (cap == null || cap.animation == null) return EventResult.pass();
                cap.frameIndex = cap.frameIndex % cap.animation.frames.size();

                boolean isUsedArm = cap.usedHand == hand;
                HumanoidArm arm = armRenderer.side();
                boolean isRightArm = arm == HumanoidArm.RIGHT;

                // Functions to extract the correct PartStates
                Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> relPartState =
                        isUsedArm ? frame -> frame.fpUsedArm : frame -> frame.fpUnusedArm;
                Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> exactPartState =
                        isRightArm ? frame -> frame.fpRightArm : frame -> frame.fpLeftArm;

                HumanoidPoseAnimation animation = cap.animation;
                HumanoidPoseAnimation.FrameMultiplier leftHandMult = isRightArm ? null : animation.leftHandMultiplier;

                if (animation.initialPose != null && animation.frames.isEmpty()) {
                    if (animation.resetFirstPersonView) matrices.setIdentity();
                    positionMatricesToState(
                            leftHandMult,
                            relPartState.apply(animation.initialPose),
                            exactPartState.apply(animation.initialPose),
                            matrices
                    );
                    return EventResult.pass();
                }

                List<HumanoidPoseAnimation.Frame> frames = animation.frames;
                if (frames.isEmpty()) return EventResult.pass();

                if (frames.size() == 1) {
                    HumanoidPoseAnimation.Frame fm = frames.get(0);
                    positionMatricesToState(
                            leftHandMult,
                            relPartState.apply(fm),
                            exactPartState.apply(fm),
                            matrices
                    );
                    return EventResult.pass();
                }

                // Interpolation logic
                float maxLength = animation.length * 20f;
                double elapsedTime = Rendering.getGameAndPartialTime() - cap.updateTime;

                if ((animation.loops == -1 || (animation.loops > 1 && elapsedTime < maxLength * animation.loops)) && elapsedTime >= maxLength) {
                    cap.frameIndex = 0;
                    elapsedTime %= maxLength;
                } else {
                    elapsedTime = Math.min(elapsedTime, maxLength);
                }

                int currentIndex = cap.frameIndex;
                while (frames.get(currentIndex).endTime * 20 < elapsedTime && currentIndex + 1 < frames.size()) {
                    currentIndex++;
                    cap.frameIndex = currentIndex;
                }

                int nextIndex = (currentIndex + 1) % frames.size();

                HumanoidPoseAnimation.PartState relState = interpolateOverFrames(frames, currentIndex, nextIndex, elapsedTime, animation.loops != 0, relPartState);
                HumanoidPoseAnimation.PartState exactState = interpolateOverFrames(frames, currentIndex, nextIndex, elapsedTime, animation.loops != 0, exactPartState);

                positionMatricesToState(leftHandMult, relState, exactState, matrices);
            }
            return EventResult.pass();
        });

        RenderEvents.LIVING_ENTITY_RENDER.register((stage, m, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage == RenderEvents.EntityRenderStage.PRE) {
                ClientPoseFacet cap = ClientPoseFacet.get(entity);
                if (cap == null) return EventResult.pass();
                if (m instanceof HumanoidModel<? extends LivingEntity> model && cap.animation != null) {
                    resetModel(model);
                }
            }
            if (stage != RenderEvents.EntityRenderStage.POSE_SETUP) return EventResult.pass();
            if (m instanceof HumanoidModel<? extends LivingEntity> model) {
                ClientPoseFacet cap = ClientPoseFacet.get(entity);
                if (cap == null) return EventResult.pass();
                if (cap.reset) {
                    cap.reset = false;
                    resetModel(model);
                }
                if (cap.animation == null) {
                    return EventResult.pass();
                }
                cap.frameIndex = cap.frameIndex % cap.animation.frames.size();

                HumanoidArm arm = cap.usedHand == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
                HumanoidPoseAnimation animation = cap.animation;
                if (animation.initialPose != null) {
                    positionModelToFrame(animation.leftHandMultiplier, animation.initialPose, model, arm, false, false); // setting initial state
                }
                if (animation.frames.size() == 1) {
                    float maxLength = animation.length * 20f;
                    double elapsedTime = Rendering.getGameTime() + Rendering.getGameAndPartialTime() - cap.updateTime;
                    positionModelToFrame(animation.leftHandMultiplier, animation.frames.get(0), model, arm, false, false); // only use first frame and avoid extra calculations if there's only one frame
                    elapsedTime = Math.min(elapsedTime, maxLength);
                    if (elapsedTime > maxLength * animation.loops && animation.nextPose != null) {
                        if (animations.get(animation.nextPose) != null) {
                            cap.animation = animations.get(animation.nextPose);
                            cap.frameIndex = 0;
                            cap.updateTime = Rendering.getGameTime();
                            cap.reset = true;
                        }
                    }
                } else if (animation.frames.size() > 0) {
                    // configuring elapsedTime (making loops work and making sure it doesn't go over maxLength)
                    float maxLength = animation.length * 20f;
                    double elapsedTime = Rendering.getGameTime() + Rendering.getGameAndPartialTime() - cap.updateTime;
                    //Nucleus.getLogger().info("" + Rendering.getGameTime() + " " + Rendering.getGameAndDeltaTime() + " " + Rendering.getGameAndPartialTime());
                    boolean anotherLoop = (animation.loops == -1 || (animation.loops > 1 && elapsedTime < maxLength * animation.loops)) && elapsedTime >= maxLength;
                    if (anotherLoop) {
                        cap.frameIndex = 0;
                        elapsedTime %= maxLength;
                    } else {
                        double maxRuntime = maxLength * animation.loops;
                        if (elapsedTime > maxRuntime) {
                            if (animation.nextPose != null) {
                                if (animations.get(animation.nextPose) != null) {
                                    cap.animation = animations.get(animation.nextPose);
                                    cap.frameIndex = 0;
                                    cap.updateTime = Rendering.getGameTime();
                                    cap.reset = true;
                                } else {
                                }
                            }
                        }
                        elapsedTime = Math.min(elapsedTime, maxLength);
                    }
                    cap.frameIndex = cap.frameIndex % cap.animation.frames.size();

                    List<HumanoidPoseAnimation.Frame> frames = animation.frames;

                    HumanoidPoseAnimation.Frame currentFrame = frames.get(cap.frameIndex);
                    while (currentFrame.endTime * 20 < elapsedTime) {
                        cap.frameIndex = (cap.frameIndex + 1) % frames.size();
                        currentFrame = frames.get(cap.frameIndex);
                    }
                    int currentIndex = cap.frameIndex;
                    int nextIndex = (currentIndex + 1) % frames.size();

                    HumanoidPoseAnimation.Frame interpolated = interpolateFrameOverTime(frames, currentIndex, nextIndex, animation.loops != 0, (float) elapsedTime);
                    positionModelToFrame(animation.leftHandMultiplier, interpolated, model, arm);

                }
            }
            return EventResult.pass();
        });
    }

    private static HumanoidPoseAnimation.Frame interpolateFrameOverTime(
            List<HumanoidPoseAnimation.Frame> frames,
            int currentIndex,
            int nextIndex,
            boolean looping,
            float delta
    ) {
        HumanoidPoseAnimation.Frame base = frames.get(currentIndex);
        HumanoidPoseAnimation.Frame result = new HumanoidPoseAnimation.Frame();

        result.head = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.head);
        result.body = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.body);

        result.usedArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.usedArm);
        result.unusedArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.unusedArm);
        result.rightArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.rightArm);
        result.leftArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.leftArm);

        result.usedItem = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.usedItem);
        result.unusedItem = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.unusedItem);
        result.rightItem = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.rightItem);
        result.leftItem = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.leftItem);

        result.rightLeg = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.rightLeg);
        result.leftLeg = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.leftLeg);

        result.fpUsedArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.fpUsedArm);
        result.fpUnusedArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.fpUnusedArm);
        result.fpRightArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.fpRightArm);
        result.fpLeftArm = interpolateOverFrames(frames, currentIndex, nextIndex, delta, looping, f -> f.fpLeftArm);

        result.interpolate = base.interpolate; // use current frame's mode
        result.endTime = base.endTime; // optional

        return result;
    }


    @Environment(EnvType.CLIENT)
    public static void positionMatricesToState(@Nullable HumanoidPoseAnimation.FrameMultiplier leftHandMult, @Nullable HumanoidPoseAnimation.PartState rel, @Nullable HumanoidPoseAnimation.PartState exact, PoseStack matrices) {
        if (leftHandMult == null) {
            if (exact != null) {
                matrices.mulPose(exact.generateMatrix());
            }
            if (rel != null) {
                matrices.mulPose(rel.generateMatrix());
            }
        } else {
            if (exact != null) {
                matrices.mulPose(exact.generateMatrix(leftHandMult));
            }
            if (rel != null) {
                matrices.mulPose(rel.generateMatrix(leftHandMult));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.Frame frame, HumanoidModel<? extends LivingEntity> model, HumanoidArm arm) {
        positionModelToFrame(leftHandMult, frame, model, arm, false, false);
    }

    @Environment(EnvType.CLIENT)
    public static void resetModel(HumanoidModel<? extends LivingEntity> model) {
        positionModelPartToState(model.head, null, false, true);
        positionModelPartToState(model.hat, null, false, true);
        positionModelPartToState(model.body, null, false, true);
        positionModelPartToState(model.leftArm, null, false, true, null);
        positionModelPartToState(model.rightArm, null, false, true);
        positionModelPartToState(model.leftArm, null, false, true);
        positionModelPartToState(model.rightArm, null, false, true);
        positionModelPartToState(model.leftLeg, null, false, true);
        positionModelPartToState(model.rightLeg, null, false, true);

        if (model instanceof PlayerModel<? extends LivingEntity> pm) {
            positionModelPartToState(pm.jacket, null, false, true);
            positionModelPartToState(pm.leftSleeve, null, false, true, null);
            positionModelPartToState(pm.rightSleeve, null, false, true);
            positionModelPartToState(pm.leftSleeve, null, false, true);
            positionModelPartToState(pm.rightSleeve, null, false, true);
            positionModelPartToState(pm.leftPants, null, false, true);
            positionModelPartToState(pm.rightPants, null, false, true);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.Frame frame, HumanoidModel<? extends LivingEntity> model, HumanoidArm arm, boolean set, boolean reset) {
        HumanoidPoseAnimation.PartState rightHandState;
        HumanoidPoseAnimation.PartState leftHandState;
        if (arm == HumanoidArm.RIGHT) {
            rightHandState = frame.usedArm;
            leftHandState = frame.unusedArm;
        } else {
            leftHandState = frame.usedArm;
            rightHandState = frame.unusedArm;
        }
        positionModelPartToState(model.head, frame.head, set, reset);
        positionModelPartToState(model.hat, frame.head, set, reset);
        positionModelPartToState(model.body, frame.body, set, reset);
        positionModelPartToState(model.leftArm, leftHandState, set, reset, leftHandMult);
        positionModelPartToState(model.rightArm, rightHandState, set, reset);
        positionModelPartToState(model.leftArm, frame.leftArm, set, reset);
        positionModelPartToState(model.rightArm, frame.rightArm, set, reset);
        positionModelPartToState(model.leftLeg, frame.leftLeg, set, reset);
        positionModelPartToState(model.rightLeg, frame.rightLeg, set, reset);

        if (model instanceof PlayerModel<? extends LivingEntity> pm) {
            positionModelPartToState(pm.jacket, frame.body, set, reset);
            //positionModelPartToState(pm., frame.body, set); //todo cape and ears
            positionModelPartToState(pm.leftSleeve, leftHandState, set, reset, leftHandMult);
            positionModelPartToState(pm.rightSleeve, rightHandState, set, reset);
            positionModelPartToState(pm.leftSleeve, frame.leftArm, set, reset);
            positionModelPartToState(pm.rightSleeve, frame.rightArm, set, reset);
            positionModelPartToState(pm.leftPants, frame.leftLeg, set, reset);
            positionModelPartToState(pm.rightPants, frame.rightLeg, set, reset);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelPartToState(ModelPart part, HumanoidPoseAnimation.PartState state, boolean set, boolean reset, HumanoidPoseAnimation.FrameMultiplier mult) {
        if (reset) {
            part.loadPose(part.getInitialPose());
            return;
        }
        if (set) {
            part.loadPose(part.getInitialPose());
        }
        if (state == null || state == HumanoidPoseAnimation.PartState.EMPTY) return;

        // Step 1: Construct current part transform as matrix
        Matrix4f partMatrix = new Matrix4f()
                .translate(part.x, part.y, part.z)
                .rotateXYZ(part.xRot, part.yRot, part.zRot)
                .scale(part.xScale, part.yScale, part.zScale);

// Step 2: Combine state and mult matrices
        Matrix4f transformMatrix = new Matrix4f()
                .mul(getMatrix(
                        new Vector3f(state.position).mul(mult.position),
                        new Vector3f(state.rotation).mul(mult.rotation),
                        new Vector3f(state.scale).mul(mult.scale)));

// Step 3: Apply transformation
        partMatrix.mul(transformMatrix);

// Step 4: Decompose the result
        Vector3f newPosition = new Vector3f();
        Vector3f newScale = new Vector3f();
        Quaternionf newRotationQuat = new Quaternionf();

        partMatrix.getTranslation(newPosition);
        partMatrix.getScale(newScale);
        partMatrix.getUnnormalizedRotation(newRotationQuat);

// Convert quaternion to Euler angles (XYZ order assumed)
        Vector3f eulerRotation = newRotationQuat.getEulerAnglesXYZ(new Vector3f());

// Step 5: Set updated values to part
        part.x = newPosition.x;
        part.y = newPosition.y;
        part.z = newPosition.z;

        part.xRot = eulerRotation.x;
        part.yRot = eulerRotation.y;
        part.zRot = eulerRotation.z;

        part.xScale = newScale.x;
        part.yScale = newScale.y;
        part.zScale = newScale.z;
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelPartToState(ModelPart part, @Nullable HumanoidPoseAnimation.PartState state, boolean set, boolean reset) {
        if (reset) {
            part.loadPose(part.getInitialPose());
            return;
        }
        if (set) {
            part.loadPose(part.getInitialPose());
        }
        if (state == null || state == HumanoidPoseAnimation.PartState.EMPTY) return;

        // Step 1: Construct current part transform as matrix
        Matrix4f partMatrix = new Matrix4f()
                .translate(part.x, part.y, part.z)
                .rotateXYZ(part.xRot, part.yRot, part.zRot)
                .scale(part.xScale, part.yScale, part.zScale);

        // Step 2: Combine state and mult matrices
        Matrix4f transformMatrix = new Matrix4f()
                .mul(state.generateMatrix());

// Step 3: Apply transformation
        partMatrix.mul(transformMatrix);

// Step 4: Decompose the result
        Vector3f newPosition = new Vector3f();
        Vector3f newScale = new Vector3f();
        Quaternionf newRotationQuat = new Quaternionf();

        partMatrix.getTranslation(newPosition);
        partMatrix.getScale(newScale);
        partMatrix.getUnnormalizedRotation(newRotationQuat);

// Convert quaternion to Euler angles (XYZ order assumed)
        Vector3f eulerRotation = newRotationQuat.getEulerAnglesXYZ(new Vector3f());

// Step 5: Set updated values to part
        part.x = newPosition.x;
        part.y = newPosition.y;
        part.z = newPosition.z;

        part.xRot = eulerRotation.x;
        part.yRot = eulerRotation.y;
        part.zRot = eulerRotation.z;

        part.xScale = newScale.x;
        part.yScale = newScale.y;
        part.zScale = newScale.z;
    }

    private static Matrix4f getMatrix(Vector3f pos, Vector3f rot, Vector3f scale) {
        return new Matrix4f()
                .translate(pos.x, pos.y, pos.z)
                .rotateXYZ(rot.x, rot.y, rot.z)
                .scale(scale.x, scale.y, scale.z);
    }
}
