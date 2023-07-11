package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.redpxnda.nucleus.util.RenderUtil.*;

public class DynamicParticle extends Particle implements DynamicParticleManager {
    public SpriteSet set;
    protected TextureAtlasSprite sprite;
    protected final Consumer<DynamicParticle> onTick;
    protected final BiConsumer<DynamicParticle, Vector3f[]> onRender;
    public float red;
    public float green;
    public float blue;
    public float alpha;
    public float scale;

    protected DynamicParticle(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender, SpriteSet set, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        this(onSetup, onTick, onRender, clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        this.set = set;
        this.pickSprite();
    }

    protected DynamicParticle(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(clientLevel, x, y, z);
        this.onTick = onTick;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.onRender = onRender;
        this.set = null;
        this.red = 1;
        this.green = 1;
        this.blue = 1;
        this.alpha = 1;
        this.scale = 1;
        this.lifetime = 100;
        onSetup.accept(this);
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public double getXSpeed() {
        return xd;
    }
    public double getYSpeed() {
        return yd;
    }
    public double getZSpeed() {
        return zd;
    }
    @Override
    public int getAge() {
        return age;
    }
    @Override
    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public float getFriction() {
        return friction;
    }
    @Override
    public float getRed() {
        return red;
    }
    @Override
    public float getGreen() {
        return green;
    }
    @Override
    public float getBlue() {
        return blue;
    }
    @Override
    public float getAlpha() {
        return alpha;
    }
    @Override
    public float getScale() {
        return scale;
    }
    @Override
    public void setFriction(float friction) {
        this.friction = friction;
    }
    @Override
    public void setRed(float r) {
        red = r;
    }
    @Override
    public void setGreen(float g) {
        green = g;
    }
    @Override
    public void setBlue(float b) {
        blue = b;
    }
    @Override
    public void setAlpha(float a) {
        alpha = a;
    }
    @Override
    public void setScale(float s) {
        scale = s;
    }
    public float getGravity() {
        return gravity;
    }
    public void setGravity(float grav) {
        this.gravity = grav;
    }
    @Override
    public boolean hasPhysics() {
        return hasPhysics;
    }
    @Override
    public void setPhysicsEnabled(boolean bl) {
        this.hasPhysics = bl;
    }
    public void setXSpeed(double speed) {
        this.xd = speed;
    }
    public void setYSpeed(double speed) {
        this.yd = speed;
    }
    public void setZSpeed(double speed) {
        this.zd = speed;
    }
    public void setSprite(TextureAtlasSprite textureAtlasSprite) {
        this.sprite = textureAtlasSprite;
    }
    public void pickSprite() {
        this.setSprite(set.get(this.random));
    }
    public void setSpriteFromAge() {
        if (!this.removed) {
            this.setSprite(set.get(this.age, this.lifetime));
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(f, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(f, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(f, this.zo, this.z) - vec3.z());
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        onRender.accept(this, vector3fs);
        scaleVectors(vector3fs, scale);
        translateVectors(vector3fs, x, y, z);
        addDoubleParticleQuad(vector3fs, vertexConsumer, red, green, blue, alpha, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), LightTexture.FULL_BRIGHT);
    }

    @Override
    public void tick() {
        super.tick();
        onTick.accept(this);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet set;
        protected BiConsumer<DynamicParticle, Vector3f[]> onRender = (p, v) -> {};
        protected Consumer<DynamicParticle> onTick = (p) -> {};
        protected Consumer<DynamicParticle> onSetup = (p) -> {};

        public Provider(SpriteSet set, BiConsumer<DynamicParticle, Vector3f[]> onRender) {
            this.set = set;
            this.onRender = onRender;
        }
        public Provider(SpriteSet set, Consumer<DynamicParticle> onTick) {
            this.set = set;
            this.onTick = onTick;
        }
        public Provider(SpriteSet set, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender) {
            this.set = set;
            this.onRender = onRender;
            this.onTick = onTick;
        }
        public Provider(SpriteSet set, Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender) {
            this.set = set;
            this.onSetup = onSetup;
            this.onRender = onRender;
            this.onTick = onTick;
        }
        public Provider(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender) {
            this.set = null;
            this.onSetup = onSetup;
            this.onRender = onRender;
            this.onTick = onTick;
        }
        public Provider(SpriteSet set) {
            this.set = set;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            if (set == null)
                return new DynamicParticle(onSetup, onTick, onRender, clientLevel, d, e, f, g, h, i);
            return new DynamicParticle(onSetup, onTick, onRender, set, clientLevel, d, e, f, g, h, i);
        }
    }
}
