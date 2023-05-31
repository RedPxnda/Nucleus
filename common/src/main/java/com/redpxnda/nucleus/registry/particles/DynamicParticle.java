package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.redpxnda.nucleus.util.RenderUtil.*;

public class DynamicParticle extends Particle {
    private final SpriteSet set;
    private TextureAtlasSprite sprite;
    protected final Consumer<DynamicParticle> onTick;
    protected final BiConsumer<DynamicParticle, Vector3f[]> onRender;
    public float red;
    public float green;
    public float blue;
    public float alpha;
    public float scale;

    protected DynamicParticle(Consumer<DynamicParticle> onSetup, Consumer<DynamicParticle> onTick, BiConsumer<DynamicParticle, Vector3f[]> onRender, SpriteSet set, ClientLevel clientLevel, double x, double y, double z) {
        super(clientLevel, x, y, z);
        this.onTick = onTick;
        this.onRender = onRender;
        this.set = set;
        pickSprite();
        this.red = 1;
        this.green = 1;
        this.blue = 1;
        this.alpha = 1;
        this.scale = 1;
        this.lifetime = 100;
        onSetup.accept(this);
    }

    public int getAge() {
        return age;
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
        private final SpriteSet set;
        private BiConsumer<DynamicParticle, Vector3f[]> onRender = (p, v) -> {};
        private Consumer<DynamicParticle> onTick = (p) -> {};
        private Consumer<DynamicParticle> onSetup = (p) -> {};

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
        public Provider(SpriteSet set) {
            this.set = set;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new DynamicParticle(onSetup, onTick, onRender, set, clientLevel, d, e, f);
        }
    }
}
