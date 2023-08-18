package com.redpxnda.nucleus.pose;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.client.Rendering;
import com.redpxnda.nucleus.datapack.codec.MiscCodecs;
import com.redpxnda.nucleus.event.RenderEvents;
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
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class PoseAnimationResourceListener extends SimpleJsonResourceReloadListener {
    public static final Map<String, HumanoidPoseAnimation> animations = new HashMap<>();

    public PoseAnimationResourceListener() {
        super(Nucleus.GSON, "poses");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profiler) {
        animations.clear();
        files.forEach((key, value) -> {
            if (!Nucleus.isNamespaceValid(key.getNamespace())) return;
            List<JsonObject> list = new ArrayList<>();
            if (value instanceof JsonObject object)
                list.add(object);
            else if (value instanceof JsonArray array)
                array.forEach(e -> {
                    if (e instanceof JsonObject object)
                        list.add(object);
                });

            list.forEach(obj -> animations.put(
                    new ResourceLocation(obj.get("name").getAsString()).toString(),
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
        RenderEvents.RENDER_ARM_WITH_ITEM.register((stage, armRenderer, player, matrices, buffer, stack, hand, partialTicks, pitch, swingProgress, equippedProgress, combinedLight) -> {
            if (stage == RenderEvents.ArmRenderStage.ARM || stage == RenderEvents.ArmRenderStage.ITEM) {
                ClientPoseCapability cap = ClientPoseCapability.getFor(player);
                if (cap == null || cap.animation == null) return EventResult.pass();

                boolean isUsedArm = cap.usedHand == hand;
                HumanoidArm arm = armRenderer.side();
                boolean isRightArm = arm == HumanoidArm.RIGHT;
                Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> relPartState = isUsedArm ? frame -> frame.fpUsedArm : frame -> frame.fpUnusedArm;
                Function<HumanoidPoseAnimation.Frame, HumanoidPoseAnimation.PartState> exactPartState = isRightArm ? frame -> frame.fpRightArm : frame -> frame.fpLeftArm;
                HumanoidPoseAnimation animation = cap.animation;

                HumanoidPoseAnimation.FrameMultiplier leftHandMult = isRightArm ? null : animation.leftHandMultiplier;

                if (animation.initialPose != null) {
                    if (animation.resetFirstPersonView) matrices.setIdentity();
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
            if (m instanceof HumanoidModel<? extends LivingEntity> model) {
                ClientPoseCapability cap = ClientPoseCapability.getFor(entity);
                if (cap == null || cap.animation == null) return EventResult.pass();

                HumanoidArm arm = cap.usedHand == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
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
    public static void positionMatricesToState(@Nullable HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.PartState rel, HumanoidPoseAnimation.PartState exact, PoseStack matrices) {
        if (leftHandMult == null) {
            matrices.translate(rel.position.x, rel.position.y, rel.position.z);
            matrices.translate(exact.position.x, exact.position.y, exact.position.z);

            Quaternionf quat = new Quaternionf();
            matrices.mulPose(quat.rotationXYZ(rel.rotation.x, rel.rotation.y, rel.rotation.z));
            matrices.mulPose(quat.rotationXYZ(exact.rotation.x, exact.rotation.y, exact.rotation.z));

            matrices.scale(rel.scale.x, rel.scale.y, rel.scale.z);
            matrices.scale(exact.scale.x, exact.scale.y, exact.scale.z);
        } else {
            matrices.translate(rel.position.x*leftHandMult.position.x, rel.position.y*leftHandMult.position.y, rel.position.z*leftHandMult.position.z);
            matrices.translate(exact.position.x*leftHandMult.position.x, exact.position.y*leftHandMult.position.y, exact.position.z*leftHandMult.position.z);

            Quaternionf quat = new Quaternionf();
            matrices.mulPose(quat.rotationXYZ(rel.rotation.x*leftHandMult.rotation.x, rel.rotation.y*leftHandMult.rotation.y, rel.rotation.z*leftHandMult.rotation.z));
            matrices.mulPose(quat.rotationXYZ(exact.rotation.x*leftHandMult.rotation.x, exact.rotation.y*leftHandMult.rotation.y, exact.rotation.z*leftHandMult.rotation.z));

            matrices.scale(rel.scale.x*leftHandMult.scale.x, rel.scale.y*leftHandMult.scale.y, rel.scale.z*leftHandMult.scale.z);
            matrices.scale(exact.scale.x*leftHandMult.scale.x, exact.scale.y*leftHandMult.scale.y, exact.scale.z*leftHandMult.scale.z);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.Frame frame, HumanoidModel<? extends LivingEntity> model, HumanoidArm arm) {
        positionModelToFrame(leftHandMult, frame, model, arm, false);
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.FrameMultiplier leftHandMult, HumanoidPoseAnimation.Frame frame, HumanoidModel<? extends LivingEntity> model, HumanoidArm arm, boolean set) {
        HumanoidPoseAnimation.PartState rightHandState;
        HumanoidPoseAnimation.PartState leftHandState;
        if (arm == HumanoidArm.RIGHT) {
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

        if (model instanceof PlayerModel<? extends LivingEntity> pm) {
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
            part.x = state.position.x*mult.position.x;
            part.y = state.position.y*mult.position.y;
            part.z = state.position.z*mult.position.z;

            part.xRot = state.rotation.x*mult.rotation.x;
            part.yRot = state.rotation.y*mult.rotation.y;
            part.zRot = state.rotation.z*mult.rotation.z;

            part.xScale = state.scale.x*mult.scale.x;
            part.yScale = state.scale.y*mult.scale.y;
            part.zScale = state.scale.z*mult.scale.z;
            return;
        }

        part.x += state.position.x*mult.position.x;
        part.y += state.position.y*mult.position.y;
        part.z += state.position.z*mult.position.z;

        part.xRot += state.rotation.x*mult.rotation.x;
        part.yRot += state.rotation.y*mult.rotation.y;
        part.zRot += state.rotation.z*mult.rotation.z;

        part.xScale *= state.scale.x*mult.scale.x;
        part.yScale *= state.scale.y*mult.scale.y;
        part.zScale *= state.scale.z*mult.scale.z;
    }
    @Environment(EnvType.CLIENT)
    public static void positionModelPartToState(ModelPart part, HumanoidPoseAnimation.PartState state, boolean set) {
        if (state == null || state == HumanoidPoseAnimation.PartState.EMPTY) return;
        if (set) {
            part.x = state.position.x;
            part.y = state.position.y;
            part.z = state.position.z;

            part.xRot = state.rotation.x;
            part.yRot = state.rotation.y;
            part.zRot = state.rotation.z;

            part.xScale = state.scale.x;
            part.yScale = state.scale.y;
            part.zScale = state.scale.z;
            return;
        }

        part.x += state.position.x;
        part.y += state.position.y;
        part.z += state.position.z;

        part.xRot += state.rotation.x;
        part.yRot += state.rotation.y;
        part.zRot += state.rotation.z;

        part.xScale *= state.scale.x;
        part.yScale *= state.scale.y;
        part.zScale *= state.scale.z;
    }
}
