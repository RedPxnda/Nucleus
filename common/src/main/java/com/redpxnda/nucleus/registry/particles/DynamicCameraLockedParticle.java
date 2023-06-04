package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.redpxnda.nucleus.util.RenderUtil.*;

public class DynamicCameraLockedParticle extends DynamicParticle {

    protected DynamicCameraLockedParticle(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender, SpriteSet set, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(onSetup, onTick, onRender, set, clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(f, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(f, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(f, this.zo, this.z) - vec3.z());
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        onRender.accept(this, vector3fs);
        rotateVectors(vector3fs, camera.rotation());
        scaleVectors(vector3fs, scale);
        translateVectors(vector3fs, x, y, z);
        addDoubleParticleQuad(vector3fs, vertexConsumer, red, green, blue, alpha, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), LightTexture.FULL_BRIGHT);
    }

    public static class Provider extends DynamicParticle.Provider {

        public Provider(SpriteSet set, BiConsumer<DynamicParticle, Vector3f[]> onRender) {
            super(set, onRender);
        }

        public Provider(SpriteSet set, Consumer<DynamicParticle> onTick) {
            super(set, onTick);
        }

        public Provider(SpriteSet set, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender) {
            super(set, onTick, onRender);
        }

        public Provider(SpriteSet set, Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender) {
            super(set, onSetup, onTick, onRender);
        }

        public Provider(SpriteSet set) {
            super(set);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new DynamicCameraLockedParticle(onSetup, onTick, onRender, set, clientLevel, d, e, f, g, h, i);
        }
    }
}
