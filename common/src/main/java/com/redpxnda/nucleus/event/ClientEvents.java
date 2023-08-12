package com.redpxnda.nucleus.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.joml.Vector2d;

@Environment(EnvType.CLIENT)
public interface ClientEvents {
    Event<ModifyCameraSensitivity> MODIFY_CAMERA_MOTION = EventFactory.createLoop(); // same as below but with modifiable motion
    Event<MiscEvents.SingleInput<Minecraft>> CAN_MOVE_CAMERA = EventFactory.createEventResult(); // called when a client player moves their camera

    interface ModifyCameraSensitivity {
        void move(Minecraft minecraft, CameraMotion motion);
    }

    class CameraMotion extends Vector2d {
        public CameraMotion(double xMotion, double yMotion) {
            x = xMotion;
            y = yMotion;
        }

        public CameraMotion() {
        }

        public double getXMotion() {
            return x;
        }

        public double getYMotion() {
            return y;
        }

        public void setMotion(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public void move(double x, double y) {
            this.x+=x;
            this.y+=y;
        }
    }
}
