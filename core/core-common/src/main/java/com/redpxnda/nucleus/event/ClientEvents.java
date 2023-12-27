package com.redpxnda.nucleus.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2d;

import java.util.Map;

@Environment(EnvType.CLIENT)
public interface ClientEvents {
    PrioritizedEvent<ModifyCameraSensitivity> MODIFY_CAMERA_MOTION = PrioritizedEvent.createLoop(); // same as below but with modifiable motion
    PrioritizedEvent<MiscEvents.SingleInput<MinecraftClient>> CAN_MOVE_CAMERA = PrioritizedEvent.createEventResult(); // called when a client player moves their camera
    PrioritizedEvent<TranslationsReloadedEvent> TRANSLATIONS_RELOADED = PrioritizedEvent.createLoop(); // fires when translations reload (but before the language is set)

    interface TranslationsReloadedEvent {
        /**
         * @param key      the key used to translate
         * @param fallback the fallback text - will normally be the same as key
         * @return null if you want do nothing, or a TranslationResult if you want to redirect it
         */
        void adjust(Map<String, String> translations);
    }

    interface ModifyCameraSensitivity {
        void move(MinecraftClient minecraft, CameraMotion motion);
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
