package com.redpxnda.nucleus.widgets.screen;

import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.math.Transform;
import com.redpxnda.nucleus.widgets.mixin.widgets.GameRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;


@SuppressWarnings("unused")
public interface MovingCinematicScreen {
    InterpolateMode START_ANIM =
            new InterpolateMode.EaseInOut(4);

    void onCloseReal();

    default Vec3 getCamPos(Vec3 initial) {
        Vec3 target = getTargetPosition();

        double targetX = target.x;
        double targetY = target.y;
        double targetZ = target.z;

        if (getAnimationTick() < 15) {
            float partial = Minecraft.getInstance()
                    .getTimer()
                    .getGameTimeDeltaPartialTick(true);

            if (isClosing()) {
                partial = -partial;
            }

            float prog = Math.min((getAnimationTick() + partial) / 15f, 1f);

            return new Vec3(
                    START_ANIM.interpolate(prog, initial.x, targetX),
                    START_ANIM.interpolate(prog, initial.y, targetY),
                    START_ANIM.interpolate(prog, initial.z, targetZ)
            );
        }
        Minecraft minecraft = Minecraft.getInstance();
        double mX = minecraft.mouseHandler.xpos();
        double mY = minecraft.mouseHandler.ypos();

        double width = minecraft.getWindow().getWidth();
        double height = minecraft.getWindow().getHeight();

        Vec2 mouse = new Vec2(
                (float) (mX - width / 2),
                (float) (mY - height / 2)
        );

        Vec2 normalized = mouse.normalized();
        float prog = mouse.length() / 2000f;

        Vec3 right = getRightVector();
        Vec3 down = getDownVector();

        Vec3 offset = right.scale(normalized.x * getLimits().xMovement)
                .add(down.scale(normalized.y * getLimits().xMovement));

        return new Vec3(
                MathUtil.lerp(prog, targetX, targetX - offset.x),
                MathUtil.lerp(prog, targetY, targetY - offset.y),
                MathUtil.lerp(prog, targetZ, targetZ - offset.z)
        );
    }

    int getAnimationTick();

    void setAnimationTick(int newTicks);

    boolean isClosing();

    void setClosing(boolean isClosing);

    boolean isCamDetached();

    /**
     * Gets the target position from the Transform.
     * <p>
     * Transform uses Vector3f internally.
     * Camera positions use Minecraft's Vec3.
     */
    default Vec3 getTargetPosition() {
        var translation = getTargetTransform().getTranslation();

        return new Vec3(translation.x(), translation.y(), translation.z());
    }

    default Matrix4f getInverseCinematicMatrix() {
        Vec3 pos = getTargetPosition();
        Vector2f angles = getCamAngles();

        float yaw = angles.x();
        float pitch = angles.y();

        return new Matrix4f()
                .translate((float) -pos.x, (float) -pos.y, (float) -pos.z)
                .rotateY((float) Math.toRadians(yaw))
                .rotateX((float) Math.toRadians(pitch));
    }

    default Vec3 getForwardVector() {
        float pitch = getTargetTransform().getRotation().x();
        float yaw = getTargetTransform().getRotation().y();

        double pitchRad = Math.toRadians(pitch);
        double yawRad = Math.toRadians(yaw);

        return new Vec3(
                -Math.sin(yawRad) * Math.cos(pitchRad),
                -Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        ).normalize();
    }

    default Vec3 getRightVector() {
        Vec3 forward = getForwardVector();
        return new Vec3(0, 1, 0)
                .cross(forward)
                .normalize();
    }

    default Vector2f getCamAngles() {
        Minecraft minecraft = Minecraft.getInstance();

        var rotation = getLookingTransform().getRotation();

        float basePitch = rotation.x();
        float baseYaw = rotation.y();

        float mouseX = (float) minecraft.mouseHandler.xpos();
        float mouseY = (float) minecraft.mouseHandler.ypos();

        float width = minecraft.getWindow().getWidth();
        float height = minecraft.getWindow().getHeight();
        //scale so the outer10% isnt sensitive af
        float screenX = Mth.clamp(
                (mouseX / width - 0.5f) / 0.4f,
                -1f,
                1f
        );

        float screenY = Mth.clamp(
                (mouseY / height - 0.5f) / 0.4f,
                -1f,
                1f
        );

        double aspect = (double) width / height;
        double fov = ((GameRendererAccessor) minecraft.gameRenderer).callHandsOnGetFov(Minecraft.getInstance().gameRenderer.getMainCamera(), 0, true);
        double verticalFov = fov;
        double horizontalFov =
                Math.toDegrees(
                        2.0 * Math.atan(
                                Math.tan(Math.toRadians(fov) / 2.0) * aspect
                        )
                );

        CinematicCameraLimits limits = getLimits();
        double maxYaw = Math.toRadians(limits.maxYaw());
        double maxPitch = Math.toRadians(limits.maxPitch());
        double yawCenterRange =
                Math.max(0.0, maxYaw - Math.toRadians(horizontalFov / 2));

        double pitchCenterRange =
                Math.max(0.0, maxPitch - Math.toRadians(verticalFov / 2));
        double yawProjection =
                screenX * Math.tan(yawCenterRange);
        double pitchProjection =
                screenY * Math.tan(pitchCenterRange);

        double yawOffset = Math.atan(yawProjection);
        double pitchOffset = Math.atan(pitchProjection);

        return new Vector2f(
                baseYaw + (float) Math.toDegrees(yawOffset),
                basePitch + (float) Math.toDegrees(pitchOffset)
        );
    }

    default Vec3 getDownVector() {
        Vec3 forward = getForwardVector();
        Vec3 right = getRightVector();
        return forward.cross(right).normalize();
    }

    default void animationTick() {
        if (getAnimationTick() < 15 && !isClosing()) {
            setAnimationTick(getAnimationTick() + 1);
        } else if (isClosing()) {
            if (getAnimationTick() > 4) {
                setAnimationTick(getAnimationTick() - 1);
            } else {
                onCloseReal();
            }
        }
    }

    Transform getTargetTransform();

    boolean isPauseScreen();

    Transform getLookingTransform();

    CinematicCameraLimits getLimits();

    record CinematicCameraLimits(float yMovement, float xMovement, float maxPitch, float maxYaw) {

    }
}
