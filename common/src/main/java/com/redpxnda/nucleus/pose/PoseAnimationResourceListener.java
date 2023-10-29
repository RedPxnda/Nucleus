package com.redpxnda.nucleus.pose;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.codec.MiscCodecs;
import com.redpxnda.nucleus.event.RenderEvents;
import com.redpxnda.nucleus.registry.NucleusNamespaces;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class PoseAnimationResourceListener extends JsonDataLoader {
    public static final Map<String, HumanoidPoseAnimation> animations = new HashMap<>();

    public PoseAnimationResourceListener() {
        super(Nucleus.GSON, "poses");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> files, ResourceManager resourceManager, Profiler profiler) {
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
                    new Identifier(obj.get("name").getAsString()).toString(),
                    MiscCodecs.quickParse(obj, HumanoidPoseAnimation.codec, s -> Nucleus.LOGGER.error("Failed to parse HumanoidPoseAnimation at {}! -> {}", key, s))
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

            Arm playerArm = player.getMainArm();
            boolean isUsedArm = (arm == playerArm && cap.usedHand == Hand.MAIN_HAND) || (arm != playerArm && cap.usedHand != Hand.MAIN_HAND);
            boolean isRightArm = arm == Arm.RIGHT;
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
                float maxLength = animation.length*20f;
                double elapsedTime = Rendering.getGameAndPartialTime()-cap.updateTime;
                if ((animation.loops == -1 || (animation.loops > 1 && elapsedTime < maxLength*animation.loops)) && elapsedTime >= maxLength) {
                    cap.frameIndex = 0;
                    elapsedTime %= maxLength;
                } else elapsedTime = Math.min(elapsedTime, maxLength);

                HumanoidPoseAnimation.Frame frame = animation.frames.get(cap.frameIndex);
                while (frame.endTime*20 < elapsedTime) {
                    cap.frameIndex++;
                    frame = animation.frames.get(cap.frameIndex);
                }
                int nextIndex = cap.frameIndex+1;
                HumanoidPoseAnimation.Frame nextFrame = nextIndex >= animation.frames.size() ? animation.frames.get(0) : animation.frames.get(nextIndex);

                float cTime = frame.endTime*20;
                float nTime = nextFrame.endTime*20;
                float delta = (float) ((elapsedTime+20 - cTime)/(nTime-cTime));

                HumanoidPoseAnimation.Frame fm = frame.interpItemTo(delta, nextFrame);
                positionMatricesToState(leftHandMult, relPartState.apply(fm), exactPartState.apply(fm), matrices);
            }
            return EventResult.pass();
        });
        RenderEvents.RENDER_ARM_WITH_ITEM.register((stage, armRenderer, player, matrices, buffer, stack, hand, partialTicks, pitch, swingProgress, equippedProgress, combinedLight) -> {
            if (stage == RenderEvents.ArmRenderStage.ARM || stage == RenderEvents.ArmRenderStage.ITEM) {
                ClientPoseFacet cap = ClientPoseFacet.get(player);
                if (cap == null || cap.animation == null) return EventResult.pass();

                boolean isUsedArm = cap.usedHand == hand;
                Arm arm = armRenderer.side();
                boolean isRightArm = arm == Arm.RIGHT;
                Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> relPartState = isUsedArm ? frame -> frame.fpUsedArm : frame -> frame.fpUnusedArm;
                Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> exactPartState = isRightArm ? frame -> frame.fpRightArm : frame -> frame.fpLeftArm;
                HumanoidPoseAnimation animation = cap.animation;

                HumanoidPoseAnimation.FrameMultiplier leftHandMult = isRightArm ? null : animation.leftHandMultiplier;

                if (animation.initialPose != null) {
                    if (animation.resetFirstPersonView) matrices.loadIdentity();
                    positionMatricesToState(leftHandMult, relPartState.apply(animation.initialPose), exactPartState.apply(animation.initialPose), matrices);
                }

                if (animation.frames.size() == 1) {
                    HumanoidPoseAnimation.Frame fm = animation.frames.get(0);
                    positionMatricesToState(leftHandMult, relPartState.apply(fm), exactPartState.apply(fm), matrices);
                } else if (animation.frames.size() > 0) {
                    float maxLength = animation.length*20f;
                    double elapsedTime = Rendering.getGameAndPartialTime()-cap.updateTime;
                    if ((animation.loops == -1 || (animation.loops > 1 && elapsedTime < maxLength*animation.loops)) && elapsedTime >= maxLength) {
                        cap.frameIndex = 0;
                        elapsedTime %= maxLength;
                    } else elapsedTime = Math.min(elapsedTime, maxLength);

                    HumanoidPoseAnimation.Frame frame = animation.frames.get(cap.frameIndex);
                    while (frame.endTime*20 < elapsedTime) {
                        cap.frameIndex++;
                        frame = animation.frames.get(cap.frameIndex);
                    }
                    int nextIndex = cap.frameIndex+1;
                    HumanoidPoseAnimation.Frame nextFrame = nextIndex >= animation.frames.size() ? animation.frames.get(0) : animation.frames.get(nextIndex);

                    float cTime = frame.endTime*20;
                    float nTime = nextFrame.endTime*20;
                    float delta = (float) ((elapsedTime+20 - cTime)/(nTime-cTime));

                    HumanoidPoseAnimation.Frame fm = frame.interpFpTo(delta, nextFrame);
                    positionMatricesToState(leftHandMult, relPartState.apply(fm), exactPartState.apply(fm), matrices);
                }
            }
            return EventResult.pass();
        });
        RenderEvents.LIVING_ENTITY_RENDER.register((stage, m, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage != RenderEvents.EntityRenderStage.POSE_SETUP) return EventResult.pass();
            if (m instanceof BipedEntityModel<? extends LivingEntity> model) {
                ClientPoseFacet cap = ClientPoseFacet.get(entity);
                if (cap == null || cap.animation == null) return EventResult.pass();

                Arm arm = cap.usedHand == Hand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
                HumanoidPoseAnimation animation = cap.animation;
                if (animation.initialPose != null)
                    positionModelToFrame(animation.leftHandMultiplier, animation.initialPose, model, arm, true); // setting initial state

                if (animation.frames.size() == 1)
                    positionModelToFrame(animation.leftHandMultiplier, animation.frames.get(0), model, arm); // only use first frame and avoid extra calculations if there's only one frame
                else if (animation.frames.size() > 0) {
                    // configuring elapsedTime (making loops work and making sure it doesn't go over maxLength)
                    float maxLength = animation.length*20f;
                    double elapsedTime = Rendering.getGameAndPartialTime()-cap.updateTime;
                    if ((animation.loops == -1 || (animation.loops > 1 && elapsedTime < maxLength*animation.loops)) && elapsedTime >= maxLength) {
                        cap.frameIndex = 0;
                        elapsedTime %= maxLength;
                    } else elapsedTime = Math.min(elapsedTime, maxLength);

                    HumanoidPoseAnimation.Frame frame = animation.frames.get(cap.frameIndex);
                    while (frame.endTime*20 < elapsedTime) {
                        cap.frameIndex++;
                        frame = animation.frames.get(cap.frameIndex);
                    }
                    int nextIndex = cap.frameIndex+1;
                    HumanoidPoseAnimation.Frame nextFrame = nextIndex >= animation.frames.size() ? animation.frames.get(0) : animation.frames.get(nextIndex);

                    float cTime = frame.endTime*20;
                    float nTime = nextFrame.endTime*20;
                    float delta = (float) ((elapsedTime+20 - cTime)/(nTime-cTime));
                    positionModelToFrame(animation.leftHandMultiplier, frame.interpTo(delta, nextFrame), model, arm);
                }
            }
            return EventResult.pass();
        });
    }

    @Environment(EnvType.CLIENT)
    public static void positionMatricesToState(@Nullable HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.PartState rel, HumanoidPoseAnimation.PartState exact, MatrixStack matrices) {
        if (leftHandMult == null) {
            matrices.multiplyPositionMatrix(exact.generateMatrix());
            matrices.multiplyPositionMatrix(rel.generateMatrix());
        } else {
            matrices.multiplyPositionMatrix(exact.generateMatrix(leftHandMult));
            matrices.multiplyPositionMatrix(rel.generateMatrix(leftHandMult));
        }
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.Frame frame, BipedEntityModel<? extends LivingEntity> model, Arm arm) {
        positionModelToFrame(leftHandMult, frame, model, arm, false);
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.Frame frame, BipedEntityModel<? extends LivingEntity> model, Arm arm, boolean set) {
        HumanoidPoseAnimation.PartState rightHandState;
        HumanoidPoseAnimation.PartState leftHandState;
        if (arm == Arm.RIGHT) {
            rightHandState = frame.usedArm;
            leftHandState = frame.unusedArm;
        } else {
            leftHandState = frame.usedArm;
            rightHandState = frame.unusedArm;
        }
        positionModelPartToState(model.head, frame.head, set);
        positionModelPartToState(model.hat, frame.head, set);
        positionModelPartToState(model.body, frame.body, set);
        positionModelPartToState(model.leftArm, leftHandState, set, leftHandMult);
        positionModelPartToState(model.rightArm, rightHandState, set);
        positionModelPartToState(model.leftArm, frame.leftArm, set);
        positionModelPartToState(model.rightArm, frame.rightArm, set);
        positionModelPartToState(model.leftLeg, frame.leftLeg, set);
        positionModelPartToState(model.rightLeg, frame.rightLeg, set);

        if (model instanceof PlayerEntityModel<? extends LivingEntity> pm) {
            positionModelPartToState(pm.jacket, frame.body, set);
            //positionModelPartToState(pm., frame.body, set); //todo cape and ears
            positionModelPartToState(pm.leftSleeve, leftHandState, set, leftHandMult);
            positionModelPartToState(pm.rightSleeve, rightHandState, set);
            positionModelPartToState(pm.leftSleeve, frame.leftArm, set);
            positionModelPartToState(pm.rightSleeve, frame.rightArm, set);
            positionModelPartToState(pm.leftPants, frame.leftLeg, set);
            positionModelPartToState(pm.rightPants, frame.rightLeg, set);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelPartToState(ModelPart part, HumanoidPoseAnimation.PartState state, boolean set, HumanoidPoseAnimation.FrameMultiplier mult) {
        if (state == null || state == HumanoidPoseAnimation.PartState.EMPTY) return;
        if (set) {
            part.pivotX = state.position.x*mult.position.x;
            part.pivotY = state.position.y*mult.position.y;
            part.pivotZ = state.position.z*mult.position.z;

            part.pitch = state.rotation.x*mult.rotation.x;
            part.yaw = state.rotation.y*mult.rotation.y;
            part.roll = state.rotation.z*mult.rotation.z;

            part.xScale = state.scale.x*mult.scale.x;
            part.yScale = state.scale.y*mult.scale.y;
            part.zScale = state.scale.z*mult.scale.z;
            return;
        }

        part.pivotX += state.position.x*mult.position.x;
        part.pivotY += state.position.y*mult.position.y;
        part.pivotZ += state.position.z*mult.position.z;

        part.pitch += state.rotation.x*mult.rotation.x;
        part.yaw += state.rotation.y*mult.rotation.y;
        part.roll += state.rotation.z*mult.rotation.z;

        part.xScale *= state.scale.x*mult.scale.x;
        part.yScale *= state.scale.y*mult.scale.y;
        part.zScale *= state.scale.z*mult.scale.z;
    }
    @Environment(EnvType.CLIENT)
    public static void positionModelPartToState(ModelPart part, HumanoidPoseAnimation.PartState state, boolean set) {
        if (state == null || state == HumanoidPoseAnimation.PartState.EMPTY) return;
        if (set) {
            part.pivotX = state.position.x;
            part.pivotY = state.position.y;
            part.pivotZ = state.position.z;

            part.pitch = state.rotation.x;
            part.yaw = state.rotation.y;
            part.roll = state.rotation.z;

            part.xScale = state.scale.x;
            part.yScale = state.scale.y;
            part.zScale = state.scale.z;
            return;
        }

        part.pivotX += state.position.x;
        part.pivotY += state.position.y;
        part.pivotZ += state.position.z;

        part.pitch += state.rotation.x;
        part.yaw += state.rotation.y;
        part.roll += state.rotation.z;

        part.xScale *= state.scale.x;
        part.yScale *= state.scale.y;
        part.zScale *= state.scale.z;
    }
}
