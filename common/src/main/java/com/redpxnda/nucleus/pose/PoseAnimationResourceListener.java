package com.redpxnda.nucleus.pose;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.codec.MiscCodecs;
import com.redpxnda.nucleus.event.RenderEvents;
import com.redpxnda.nucleus.util.RenderUtil;
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
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        RenderEvents.CHANGE_RENDERED_HANDS.register((player, hands) -> {
            hands.setOffhand(true);
        });
        RenderEvents.LIVING.register((stage, m, entity, entityYaw, partialTick, matrixStack, multiBufferSource, packedLight) -> {
            if (stage != RenderEvents.Stage.POSE_SETUP) return EventResult.pass();
            if (m instanceof HumanoidModel<? extends LivingEntity> model) {
                ClientPoseCapability cap = ClientPoseCapability.getFor(entity);
                if (cap == null || cap.animation == null) return EventResult.pass();

                HumanoidPoseAnimation animation = cap.animation;
                if (animation.initialPose != null)
                    positionModelToFrame(animation.initialPose, model, true); // setting initial state

                if (animation.frames.size() == 1)
                    positionModelToFrame(animation.frames.get(0), model); // only use first frame and avoid extra calculations if there's only one frame
                else if (animation.frames.size() > 0) {
                    // configuring elapsedTime (making loops work and making sure it doesn't go over maxLength)
                    float maxLength = animation.length*20f;
                    double elapsedTime = RenderUtil.getGameAndPartialTime()-cap.updateTime;
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
                    positionModelToFrame(frame.interpTo(delta, nextFrame), model);
                }
            }
            return EventResult.pass();
        });
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.Frame frame, HumanoidModel<? extends LivingEntity> model) {
        positionModelToFrame(frame, model, false);
    }

    @Environment(EnvType.CLIENT)
    public static void positionModelToFrame(HumanoidPoseAnimation.Frame frame, HumanoidModel<? extends LivingEntity> model, boolean set) {
        positionModelPartToState(model.head, frame.head, set);
        positionModelPartToState(model.hat, frame.head, set);
        positionModelPartToState(model.body, frame.body, set);
        positionModelPartToState(model.leftArm, frame.leftArm, set);
        positionModelPartToState(model.rightArm, frame.rightArm, set);
        positionModelPartToState(model.leftLeg, frame.leftLeg, set);
        positionModelPartToState(model.rightLeg, frame.rightLeg, set);

        if (model instanceof PlayerModel<? extends LivingEntity> pm) {
            positionModelPartToState(pm.jacket, frame.body, set);
            //positionModelPartToState(pm., frame.body, set);
            positionModelPartToState(pm.leftSleeve, frame.leftArm, set);
            positionModelPartToState(pm.rightSleeve, frame.rightArm, set);
            positionModelPartToState(pm.leftPants, frame.leftLeg, set);
            positionModelPartToState(pm.rightPants, frame.rightLeg, set);
        }
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
