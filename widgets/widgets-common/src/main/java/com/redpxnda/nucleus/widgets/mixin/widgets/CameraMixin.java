package com.redpxnda.nucleus.widgets.mixin.widgets;

import com.redpxnda.nucleus.widgets.screen.MovingCinematicScreen;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setPosition(Vec3 position);

    @Shadow
    protected abstract void setRotation(float xRot, float yRot);

    @Shadow
    private boolean detached;

    @Shadow
    public abstract Vec3 getPosition();

    @Shadow
    @Final
    private Quaternionf rotation;

    @Inject(method = "setup", at = @At("TAIL"))
    private void postCameraSetup(
            BlockGetter blockGetter,
            Entity entity,
            boolean bl,
            boolean bl2,
            float partialTick,
            CallbackInfo ci
    ) {
        if (!(Minecraft.getInstance().screen instanceof MovingCinematicScreen screen))
            return;

        Vec3 position = screen.getCamPos(getPosition());
        setPosition(position);
        Vector2f camAngles = screen.getCamAngles();
        setRotation(camAngles.x(), camAngles.y());
        detached = screen.isCamDetached();
    }
}