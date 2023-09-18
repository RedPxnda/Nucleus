package com.redpxnda.nucleus.registry.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

public class ChunkParticle extends DynamicTextureSheetParticle {
    public float uo, vo;

    protected ChunkParticle(ChunkParticleOptions options, ClientWorld clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(null, RenderLayer.getSolid(), clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        applyOptions(options);
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2f;
        this.setPhysicsEnabled(true);
        this.gravityStrength = 1f;
    }

    @Override
    public void applyOptions(DynamicParticleOptions options) {
        super.applyOptions(options);
        if (options instanceof ChunkParticleOptions o) {
            this.sprite = o.block == null ?
                    MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(o.texture) :
                    MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(o.block.getDefaultState());
        }
    }

    @Override
    public float getU0() {
        return this.sprite.getFrameU((this.uo + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    public float getU1() {
        return this.sprite.getFrameU(this.uo / 4.0f * 16.0f);
    }

    @Override
    public float getV0() {
        return this.sprite.getFrameV(this.vo / 4.0f * 16.0f);
    }

    @Override
    public float getV1() {
        return this.sprite.getFrameV((this.vo + 1.0f) / 4.0f * 16.0f);
    }

    public static class Provider implements ParticleFactory<ChunkParticleOptions> {

        @Nullable
        @Override
        public Particle createParticle(ChunkParticleOptions particleOptions, ClientWorld clientLevel, double d, double e, double f, double g, double h, double i) {
            return new ChunkParticle(particleOptions, clientLevel, d, e, f, g, h, i);
        }
    }
}
