package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.client.Rendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

import static com.redpxnda.nucleus.Nucleus.loc;

public class CubeParticle extends DynamicPoseStackParticle {
    public static Sprite sprite = null;

    public boolean invert = false;
    public float xSize = 1;
    public float ySize = 1;
    public float zSize = 1;

    protected CubeParticle(CubeParticleOptions options, ClientWorld clientLevel, double x, double y, double z, double dx, double dy, double dz) {
        super(null, RenderLayer.getTranslucent(), clientLevel, x, y, z, dx, dy, dz);
        applyOptions(options);

        spacingY = scale;
        spacingXZ = scale;

        if (sprite == null) sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(loc("item/blank"));
    }

    @Override
    public void setScale(float s) {
        super.setScale(s);
        spacingY = scale;
        spacingXZ = scale;
    }

    @Override
    public void applyOptions(DynamicParticleOptions options) {
        super.applyOptions(options);
        if (options instanceof CubeParticleOptions cbop) {
            invert = cbop.invert;
            xSize = cbop.xSize;
            ySize = cbop.ySize;
            zSize = cbop.zSize;
        }
    }

    @Override
    public void render(VertexConsumer vc, MatrixStack stack, float x, float y, float z, Camera camera, float partialTick) {
        super.render(vc, stack, x, y, z, camera, partialTick);
        float xSize = invert ? this.xSize : -this.xSize;
        for (int i = 0; i < 6; i++) {
            Rendering.addQuad(
                    Rendering.CUBE[i], stack, vc,
                    red, green, blue, alpha,
                    xSize, ySize, zSize,
                    sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(),
                    LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE);
        }
    }

    public static class Provider implements ParticleFactory<CubeParticleOptions> {
        @Nullable
        @Override
        public Particle createParticle(CubeParticleOptions particleOptions, ClientWorld clientLevel, double d, double e, double f, double g, double h, double i) {
            return new CubeParticle(particleOptions, clientLevel, d, e, f, g, h, i);
        }
    }
}
